package com.cropdeal.orderservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.model.Product;
import com.cropdeal.orderservice.repository.OrderRepository;

@Service
public class OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OrderRepository repository;

	@Autowired
	private CartService cartService;

	@Autowired
	private ReceiptService receiptService;

	@Autowired
	private PaymentService paymentService;

	private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";

	public List<Order> getAllOrders() {
		return repository.findAll();
	}

	public Order getOrderById(String id) throws InvalidOrderException {
		return repository.findById(id).orElseThrow(() -> new InvalidOrderException("Invalid order ID: " + id));
	}

	public Receipt placeOrderFromCart(String dealerId)
			throws CartNotFoundException, PaymentNotDoneException {
		// Retrieve the dealer's cart
		Cart cart = cartService.getCartByDealerId(dealerId);

		// Create a new order
		Order order = new Order();
		order.setDealerId(dealerId);
		order.setOrderItems(cart.getCartItems());

		// Clear the dealer's cart
		cartService.clearCart(dealerId);

		return createOrder(order);
	}

	public Receipt placeOrderDirectly(String dealerId, String productId, int quantity)
			throws InvalidProductException, PaymentNotDoneException {
		// Create a new order
		Map<String, Integer> orderItems = Map.of(productId, quantity);
		Order order = new Order(dealerId, orderItems);

		// Generate and set the orderId
		String orderId = generateOrderId();
		order.setOrderId(orderId);

		// Save the order to the database
		return createOrder(order);
	}

	public Receipt createOrder(Order order) throws PaymentNotDoneException {
		double amount = calculateTotalPrice(order.getOrderItems());

		// Process payment and get the payment ID
//		String paymentId = paymentService.processPayment(amount, order.getOrderId());
//
//		// Check if the payment was successful
//		boolean isPaymentDone = paymentService.checkPaymentStatus(paymentId);

		boolean isPaymentDone = true;

		if (isPaymentDone) {
			for (Map.Entry<String, Integer> orderItemEntry : order.getOrderItems().entrySet()) {
				String productId = orderItemEntry.getKey();
				int quantity = orderItemEntry.getValue();
				updateInventory(productId, quantity);
			}

			// Save the order to the database
			Order placedOrder = repository.save(order);
			log.info("Order created with ID: {}", placedOrder.getOrderId());

			// Create the receipt
			Receipt receipt = new Receipt();
			receipt.setOrderId(placedOrder.getOrderId());
			receipt.setDealerId(order.getDealerId());
			receipt.setOrderItems(order.getOrderItems());
			receipt.setTotalPrice(amount);
			receipt.setStatus("Placed");
//			receipt.setRazorpayOrderId(paymentId);

			return receiptService.createReceipt(receipt);
		} else {
			// Handle the case when payment is not done
			throw new PaymentNotDoneException("Payment is not done for the order");
		}
	}

	public void cancelOrder(String orderId) throws InvalidOrderException, ReceiptNotFoundException, PaymentNotDoneException {
		Order order = getOrderById(orderId);
		Receipt receipt = receiptService.getReceiptByOrderId(orderId);

		// Process payment refund if the order has been paid
		if (receipt.getStatus().equals("Paid")) {
			paymentService.processPaymentRefund(receipt.getRazorpayOrderId());
		}

		// Update inventory for each order item
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

	public Order updateOrder(String orderId, Order updatedOrder) throws InvalidOrderException, ReceiptNotFoundException, PaymentNotDoneException {
		Order order = getOrderById(orderId);
		Receipt receipt = receiptService.getReceiptByOrderId(orderId);

		// Process payment adjustment if the order has been paid
		if (receipt.getStatus().equals("Paid")) {
			paymentService.processPaymentAdjustment(receipt.getRazorpayOrderId(), calculateTotalPrice(updatedOrder.getOrderItems()));
		}

		order.setOrderItems(updatedOrder.getOrderItems());
		order = repository.save(order);
		log.info("Order updated with ID: {}", orderId);
		return order;
	}

	private void updateInventory(String productId, int quantity) {
		String url = INVENTORY_SERVICE_URL + "/products/" + productId + "/updateQuantity?quantity=" + quantity;
		restTemplate.put(url, null);
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

	private Product getProductById(String productId) {
		String url = INVENTORY_SERVICE_URL + "/products/" + productId;
		return restTemplate.getForObject(url, Product.class);
	}

	public String generateOrderId() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timestamp = now.format(formatter);

		Random random = new Random();
		int randomNumber = random.nextInt(10000);

		return timestamp + randomNumber;
	}
}
