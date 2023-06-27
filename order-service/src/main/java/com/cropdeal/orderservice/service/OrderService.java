package com.cropdeal.orderservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.model.Product;
import com.cropdeal.orderservice.repository.OrderRepository;

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

//	@Autowired
//	private PaymentService paymentService;
	
	private static final String CIRCUIT_BREAKER_NAME = "inventoryServiceCircuitBreaker";
	private static final String RETRY_NAME = "inventoryServiceRetry";
	private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";

	public List<Order> getAllOrders() {
		return repository.findAll();
	}

	public Order getOrderById(String orderId) throws InvalidOrderException {
		return repository.findById(orderId)
				.orElseThrow(() -> new InvalidOrderException("Invalid order ID: " + orderId));
	}

	public Receipt placeOrderFromCart() throws CartNotFoundException, PaymentNotDoneException {
		String dealerId = retrieveUserId();
		Cart cart = cartService.getCartByDealerId(dealerId);

		Order order = new Order();
		order.setDealerId(dealerId);
		order.setOrderItems(cart.getCartItems());

		cartService.clearCart();

		return createOrder(order);
	}

	public Receipt placeOrderDirectly(String productId, int quantity)
			throws PaymentNotDoneException {
		Map<String, Integer> orderItems = Map.of(productId, quantity);
		Order order = new Order(retrieveUserId(), orderItems);

		String orderId = generateOrderId();
		order.setOrderId(orderId);

		return createOrder(order);
	}

	public Receipt createOrder(Order order) throws PaymentNotDoneException {
		double amount = calculateTotalPrice(order.getOrderItems());
		
		boolean isPaymentDone = true;

		if (isPaymentDone) {
			for (Map.Entry<String, Integer> orderItemEntry : order.getOrderItems().entrySet()) {
				String productId = orderItemEntry.getKey();
				int quantity = orderItemEntry.getValue();
				updateInventory(productId, quantity);
			}

			Order placedOrder = repository.save(order);
			log.info("Order created with ID: {}", placedOrder.getOrderId());

			Receipt receipt = new Receipt();
			receipt.setOrderId(placedOrder.getOrderId());
			receipt.setDealerId(retrieveUserId());
			receipt.setOrderItems(order.getOrderItems());
			receipt.setTotalPrice(amount);
			receipt.setStatus("Placed");

			return receiptService.createReceipt(receipt);
		} else {
			throw new PaymentNotDoneException("Payment is not done for the order");
		}
	}

	public void cancelOrder(String orderId)
			throws InvalidOrderException, ReceiptNotFoundException{
		Order order = getOrderById(orderId);
		Receipt receipt = receiptService.getReceiptByOrderId(orderId);

//		if (receipt.getStatus().equals("Paid")) {
//			paymentService.processPaymentRefund(receipt.getRazorpayOrderId());
//		}

		for (Map.Entry<String, Integer> orderItemEntry : order.getOrderItems().entrySet()) {
			String productId = orderItemEntry.getKey();
			int quantity = orderItemEntry.getValue();
			updateInventory(productId, -1 * quantity);
		}

		receipt.setStatus("Cancelled");
		receiptService.updateReceipt(orderId, receipt);
		repository.delete(order);
		log.info("Order cancelled with ID: {}", orderId);
	}

	public Order updateOrder(String orderId, Order updatedOrder)
			throws InvalidOrderException{
		Order order = getOrderById(orderId);
//		Receipt receipt = receiptService.getReceiptByOrderId(orderId);
//
//		if (receipt.getStatus().equals("Paid")) {
//			paymentService.processPaymentAdjustment(receipt.getRazorpayOrderId(),
//					calculateTotalPrice(updatedOrder.getOrderItems()));
//		}

		order.setOrderItems(updatedOrder.getOrderItems());
		order = repository.save(order);
		log.info("Order updated with ID: {}", orderId);
		return order;
	}

	@Retry(name = RETRY_NAME, fallbackMethod = "updateInventoryFallback")
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "updateInventoryFallback")
	private void updateInventory(String productId, int quantity) {
		log.info("Updating product with id {} quantity {}", productId, quantity);
		String url = INVENTORY_SERVICE_URL + "/products/" + productId + "/updateQuantity?quantity=" + quantity;
		restTemplate.put(url, null);
		log.info("Updated inventory successfully");
	}
	
	private void updateInventoryFallback(String productId, int quantity, Throwable throwable) {
	    log.error("Failed to update inventory for product {} with quantity {}", productId, quantity);
	}

	private double calculateTotalPrice(Map<String, Integer> orderItems) {
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
    private Product getProductByIdFallback(String productId, Exception ex) {
        log.error("Error occurred while retrieving product with ID: {}. Returning fallback response.", productId);
        return null; 
    }
	
	

	public String generateOrderId() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timestamp = now.format(formatter);

		Random random = new Random();
		int randomNumber = random.nextInt(10000);

		return timestamp + randomNumber;
	}

	public String retrieveUserId() {
		String id = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("Userid retrieve : " + id);
		return id;
	}
	
	

}
