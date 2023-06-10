package com.cropdeal.orderservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidCropException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.InvalidReceiptException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.model.Crop;
import com.cropdeal.orderservice.repository.OrderRepository;

@Service
public class OrderService {

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

//	private static final String INVENTORY_SERVICE_URL = "http://inventory-service";
	private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";
//	private static final String PAYMENT_GATEWAY_URL = "https://api.paymentgateway.com";

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

	public Receipt placeOrderDirectly(String dealerId, String cropId, int quantity) throws InvalidCropException, PaymentNotDoneException {
		// Create a new order
		
		Crop crop = getCropById(cropId);
		crop.setQuantity(quantity);
		List<Crop> item = Arrays.asList(crop);
		
		Order order = new Order(dealerId, item);
		
		// Generate and set the orderId
	    String orderId = generateOrderId();
	    order.setOrderId(orderId);

		// Save the order to the database
		return createOrder(order);
	}

	public Receipt createOrder(Order order) throws PaymentNotDoneException {
		
		double amount = calculateTotalPrice(order.getOrderItems());

	    // Process payment and get the Razorpay order ID
	    String razorpayOrderId = paymentService.processPayment(amount, order.getOrderId());

	    boolean isPaymentDone = paymentService.checkPaymentStatus(razorpayOrderId);

	    if (isPaymentDone) {
			for (Crop orderItem : order.getOrderItems()) {
				updateInventory(orderItem.getId(), orderItem.getQuantity());
			}

			// Save the order to the database
			Order placedOrder = repository.save(order);
			
			// Create the receipt
	        Receipt receipt = new Receipt();
	        receipt.setOrderId(placedOrder.getOrderId());
	        receipt.setDealerId(order.getDealerId());
	        receipt.setOrderItems(order.getOrderItems());
	        receipt.setTotalPrice(amount);
	        receipt.setStatus("Placed");
	        receipt.setRazorpayOrderId(razorpayOrderId);

	        return receiptService.createReceipt(receipt);

	        // Rest of the code...
	    } else {
	        // Handle the case when payment is not done
	        // You can throw an exception, return null, or take any appropriate action based
	        // on your requirement
	        throw new PaymentNotDoneException("Payment is not done for the order");
	    }
	}



//	private boolean checkPaymentStatus(Order order) {
//		// Connect to the payment gateway API to check the payment status
//		// You can use the PAYMENT_GATEWAY_URL and appropriate API endpoints to check
//		// the payment status
//		// Implement your logic to check if the payment is done or not
//		// Return true if payment is done, false otherwise
//		// Example:
//		// String paymentStatus = restTemplate.getForObject(PAYMENT_GATEWAY_URL +
//		// "/orders/" + order.getOrderId() + "/paymentStatus", String.class);
//		// return "SUCCESS".equals(paymentStatus);
//		return true; // Placeholder for the payment check logic
//	}

	public void cancelOrder(String orderId) throws InvalidOrderException, InvalidReceiptException {
//		Order order = getOrderById(orderId);
//		Receipt receipt = receiptService.getReceiptByOrderId(orderId);
//
//		// Process payment refund if the order has been paid
//		if (receipt.getStatus().equals("Paid")) {
//			paymentService.processPaymentRefund(order);
//		}
//
//		// Update inventory for each order item
//		for (Crop orderItem : order.getOrderItems()) {
//			updateInventory(orderItem.getId(), -1 * orderItem.getQuantity());
//		}
//		
//		receipt.setStatus("Cancelled");
//		receiptService.createReceipt(receipt);
//		repository.delete(order);
	}

	public Order updateOrder(String orderId, Order updatedOrder) throws InvalidOrderException, InvalidReceiptException {
		Order order = getOrderById(orderId);
//		Receipt receipt = receiptService.getReceiptByOrderId(orderId);
//
//		// Process payment adjustment if the order has been paid
//		if (receipt.getStatus().equals("Paid")) {
//			paymentService.processPaymentAdjustment(order, updatedOrder);
//		}
//
//		order.setOrderItems(updatedOrder.getOrderItems());
		return repository.save(order);
	}


	private void updateInventory(String cropId, int quantity) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Integer> requestEntity = new HttpEntity<>(quantity, headers);

		String url = INVENTORY_SERVICE_URL + "/crops/" + cropId + "/updateQuantity";
		restTemplate.put(url, requestEntity);
	}

	private double calculateTotalPrice(List<Crop> orderItems) {
		double totalPrice = 0;
		for (Crop orderItem : orderItems) {
			Crop crop = getCropById(orderItem.getId());
			double itemPrice = crop.getPrice() * orderItem.getQuantity();
			totalPrice += itemPrice;
		}
		return totalPrice;
	}

	private Crop getCropById(String cropId) {
		String url = INVENTORY_SERVICE_URL + "/crops/" + cropId;
		return restTemplate.getForObject(url, Crop.class);
	}
	
	public String generateOrderId() {
	    // Generate a timestamp
	    LocalDateTime now = LocalDateTime.now();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	    String timestamp = now.format(formatter);

	    // Generate a random number
	    Random random = new Random();
	    int randomNumber = random.nextInt(10000);

	    // Combine timestamp and random number to create the order ID
	    return timestamp + randomNumber;
	}
}
