package com.cropdeal.orderservice.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.entity.Transaction;
import com.cropdeal.orderservice.entity.TransactionType;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.model.Product;
import com.cropdeal.orderservice.repository.OrderRepository;
import com.razorpay.RazorpayException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class OrderService {

	Logger log = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OrderRepository repository;

	@Autowired
	private CartService cartService;

	@Autowired
	private ReceiptService receiptService;

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private PaymentService paymentService;
	
	private static final String CIRCUIT_BREAKER_NAME = "inventoryServiceCircuitBreaker";
	private static final String RETRY_NAME = "inventoryServiceRetry";
	private static final String INVENTORY_SERVICE_URL =
			"http://inventory-service";
	
	public List<Order> getAllOrders() {
		return repository.findAll();
	}

	public Order getOrderById(String orderId) throws InvalidOrderException {
		return repository.findById(orderId)
				.orElseThrow(() -> new InvalidOrderException("Invalid order ID: " + orderId));
	}

	public Order placeOrder(
			String dealerId,
			String addressId
	) throws CartNotFoundException,
			NoSuchAlgorithmException {

		Cart cart = cartService.getCartByDealerId(dealerId);

		Order order = new Order();

		order.setOrderId(generateOrderId());

		order.setDealerId(dealerId);

		order.setOrderItems(
				new HashMap<>(
						cart.getCartItems()
				)
		);

		order.setTotalPrice(cart.getTotalPrice());

		order.setDeliveryAddressId(addressId);

		order.setStatus("Pending");
		log.info("Pending order: {}", order);

		return repository.save(order);
	}

	public Receipt createOrder(String orderId, String paymentId) throws PaymentNotDoneException, InvalidOrderException, RazorpayException  {
		
		log.info("Creating order for order id : {}", orderId);
		boolean isPaid = paymentService.verifyPayment(paymentId);
		
		Order order = getOrderById(orderId);
		
		if (isPaid) {
			
			order.setStatus("Placed");
			
			repository.save(order);
			
			for (Map.Entry<String, Integer> orderItemEntry : order.getOrderItems().entrySet()) {
				String productId = orderItemEntry.getKey();
				int quantity = orderItemEntry.getValue();
				updateInventory(productId, quantity);
			}
			
			log.info("Order plcaed with ID: {}", orderId);
			
			Receipt receipt = new Receipt();
			receipt.setOrderId(order.getOrderId());
			receipt.setDealerId(order.getDealerId());
			receipt.setOrderItems(order.getOrderItems());
			receipt.setTotalPrice(order.getTotalPrice());
			receipt.setStatus("Paid");
			
			paymentService.addTransaction(order, paymentId);
			
			receipt.setTransactionId(paymentId);
			receiptService.createReceipt(receipt);
			
			

			return receipt;
		}
		return null;
	}

	public void cancelOrder(String orderId) throws InvalidOrderException, ReceiptNotFoundException {
		Order order = getOrderById(orderId);
		Receipt receipt = receiptService.getReceiptByOrderId(orderId);

		for (Map.Entry<String, Integer> orderItemEntry : order.getOrderItems().entrySet()) {
			String productId = orderItemEntry.getKey();
			int quantity = orderItemEntry.getValue();
			updateInventory(productId, -1 * quantity);
		}

		receipt.setStatus("Cancelled");
		receiptService.updateReceipt(orderId, receipt);
		repository.delete(order);
		log.info("Order cancelled with ID: {}", orderId);

		// Create a transaction entry for cancellation
		Transaction transaction = new Transaction();
		transaction.setId(receipt.getTransactionId());
		transaction.setId(receipt.getOrderId());
		transaction.setAmount(receipt.getTotalPrice());
		transaction.setType(TransactionType.CREDIT); // Refund
		transaction.setUsername(retrieveUserId());
		transaction.setTimestamp(LocalDateTime.now());

		transactionService.createTransaction(transaction);
	}

	@Retry(name = RETRY_NAME, fallbackMethod = "updateInventoryFallback")
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "updateInventoryFallback")
	private void updateInventory(String productId, int quantity) {
		log.info("Updating product with id {} quantity {}", productId, quantity);
		String url = INVENTORY_SERVICE_URL + "/products/" + productId + "/updateQuantity?quantity=" + quantity;
		restTemplate.put(url, null);
		log.info("Updated inventory successfully");
	}
	
	@SuppressWarnings("unused")
	private void updateInventoryFallback(String productId, int quantity, Throwable throwable) {
	    log.error("Failed to update inventory for product {} with quantity {}", productId, quantity);
	}

	public double calculateTotalPrice(Map<String, Integer> orderItems) {
		double totalPrice = 0;
		for (Map.Entry<String, Integer> orderItemEntry : orderItems.entrySet()) {
			String productId = orderItemEntry.getKey();
			int quantity = orderItemEntry.getValue();
			Product product = getProductById(productId);
			double itemPrice = product.getPrice() * quantity;
			totalPrice += itemPrice;
		}
		return totalPrice;
	}

	@Retry(name = RETRY_NAME, fallbackMethod = "getProductByIdFallback")
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getProductByIdFallback")
    public Product getProductById(String productId) {
        log.info("Retrieving product using RestTemplate...");
        String url = INVENTORY_SERVICE_URL + "/products/findById/" + productId;
        Product product = restTemplate.getForObject(url, Product.class);
        log.info("Obtained product {} using RestTemplate", product);
        return product;
    }

    // Fallback method for Circuit Breaker and Retry
    @SuppressWarnings("unused")
	private Product getProductByIdFallback(String productId, Exception ex) {
        log.error("Error occurred while retrieving product with ID: {}. Returning fallback response.", productId);
        return null; 
    }
	
	

	public String generateOrderId() throws NoSuchAlgorithmException {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timestamp = now.format(formatter);

		Random random = SecureRandom.getInstanceStrong();
		int randomNumber = random.nextInt(10000);

		return timestamp + randomNumber;
	}

	public String retrieveUserId() {
		String id = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info("Userid retrieve : {}", id);
		return id;
	}

	public List<Order> getOrderByDealerId(String id) {
		return repository.findByDealerId(id);	
	}

}
