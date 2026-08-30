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
import com.cropdeal.orderservice.model.Product;
import com.cropdeal.orderservice.repository.OrderRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);
	private static final String CIRCUIT_BREAKER_NAME = "inventoryServiceCircuitBreaker";
	private static final String RETRY_NAME = "inventoryServiceRetry";
	private static final String INVENTORY_SERVICE_URL = "http://inventory-service";

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

	public List<Order> getAllOrders() {
		return repository.findAll();
	}

	public Order getOrderById(String orderId) throws InvalidOrderException {
		return repository.findById(orderId)
				.orElseThrow(() -> new InvalidOrderException("Invalid order ID: " + orderId));
	}

	public Order placeOrder(String dealerId, String addressId) throws CartNotFoundException, NoSuchAlgorithmException {
		Cart cart = cartService.getCartByDealerId(dealerId);

		if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
			throw new CartNotFoundException("Cannot place an order with an empty cart");
		}

		if (cart.getTotalPrice() <= 0) {
			throw new CartNotFoundException("Cart total must be greater than zero");
		}

		Order order = new Order();
		order.setOrderId(generateOrderId());
		order.setDealerId(dealerId);
		order.setOrderItems(new HashMap<>(cart.getCartItems()));
		order.setTotalPrice(cart.getTotalPrice());
		order.setDeliveryAddressId(addressId);
		order.setStatus("Pending");
		order.setPaymentStatus("Pending");
		order.setPaymentMode(null);
		order.setTransactionId(null);
		order.setRazorpayOrderId(null);

		Order savedOrder = repository.save(order);

		log.info("Created pending CropDeal order {} for dealer {}", savedOrder.getOrderId(), dealerId);

		return savedOrder;
	}

	public Receipt completePaidOrder(String orderId, String paymentId, String paymentMode) throws InvalidOrderException, CartNotFoundException {
		Order order = getOrderById(orderId);

		if ("Placed".equalsIgnoreCase(order.getStatus()) || "Done".equalsIgnoreCase(order.getPaymentStatus())) {
			throw new InvalidOrderException("Order has already been completed");
		}

		if (!"Pending".equalsIgnoreCase(order.getStatus())) {
			throw new InvalidOrderException("Only pending orders can be completed");
		}

		if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
			throw new InvalidOrderException("Order contains no products");
		}

		for (Map.Entry<String, Integer> entry : order.getOrderItems().entrySet()) {
			String productId = entry.getKey();
			Integer quantity = entry.getValue();

			if (quantity == null || quantity <= 0) {
				throw new InvalidOrderException("Invalid quantity for product: " + productId);
			}

			updateInventory(productId, quantity);
		}

		order.setStatus("Placed");
		order.setPaymentStatus("Done");
		order.setPaymentMode(paymentMode);
		order.setTransactionId(paymentId);

		repository.save(order);

		Transaction transaction = new Transaction();
		transaction.setPaymentId(paymentId);
		transaction.setAmount(order.getTotalPrice());
		transaction.setUsername(order.getDealerId());
		transaction.setTimestamp(LocalDateTime.now());
		transaction.setType(TransactionType.DEBIT);

		transactionService.createTransaction(transaction);

		Receipt receipt = new Receipt();
		receipt.setOrderId(order.getOrderId());
		receipt.setDealerId(order.getDealerId());
		receipt.setOrderItems(new HashMap<>(order.getOrderItems()));
		receipt.setTotalPrice(order.getTotalPrice());
		receipt.setStatus("Paid");
		receipt.setTransactionId(paymentId);

		Receipt savedReceipt = receiptService.createReceipt(receipt);

		cartService.clearCart(order.getDealerId());

		log.info("Order {} placed successfully. Payment {} completed using {}", orderId, paymentId, paymentMode);

		return savedReceipt;
	}

	public void cancelOrder(String orderId) throws InvalidOrderException {
		Order order = getOrderById(orderId);

		if ("Placed".equalsIgnoreCase(order.getStatus()) || "Done".equalsIgnoreCase(order.getPaymentStatus())) {
			throw new InvalidOrderException("Paid orders require a Razorpay refund flow");
		}

		repository.delete(order);

		log.info("Pending order {} cancelled", orderId);
	}

	private void updateInventory(String productId, int quantity) {
		log.info("Reducing inventory for product {} by {}", productId, quantity);

		String url = INVENTORY_SERVICE_URL + "/products/" + productId + "/updateQuantity?quantity=" + quantity;

		restTemplate.put(url, null);

		log.info("Inventory updated successfully for product {}", productId);
	}

	public double calculateTotalPrice(Map<String, Integer> orderItems) {
		double totalPrice = 0;

		for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
			String productId = entry.getKey();
			int quantity = entry.getValue();
			Product product = getProductById(productId);

			if (product == null) {
				throw new IllegalStateException("Unable to retrieve product: " + productId);
			}

			totalPrice += product.getPrice() * quantity;
		}

		return totalPrice;
	}

	@Retry(name = RETRY_NAME, fallbackMethod = "getProductByIdFallback")
	@CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getProductByIdFallback")
	public Product getProductById(String productId) {
		String url = INVENTORY_SERVICE_URL + "/products/findById/" + productId;
		return restTemplate.getForObject(url, Product.class);
	}

	@SuppressWarnings("unused")
	private Product getProductByIdFallback(String productId, Exception exception) {
		log.error("Unable to retrieve product {} from inventory-service", productId, exception);
		return null;
	}

	public String generateOrderId() throws NoSuchAlgorithmException {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timestamp = now.format(formatter);
		Random random = SecureRandom.getInstanceStrong();
		int randomNumber = random.nextInt(10000);
		return timestamp + String.format("%04d", randomNumber);
	}

	public String retrieveUserId() {
		String id = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info("Retrieved user ID: {}", id);
		return id;
	}

	public List<Order> getOrderByDealerId(String id) {
		return repository.findByDealerId(id);
	}
}