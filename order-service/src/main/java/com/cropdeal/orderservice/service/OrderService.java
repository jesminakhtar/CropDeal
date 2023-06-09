package com.cropdeal.orderservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidCropException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.InvalidReceiptException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.model.Cart;
import com.cropdeal.orderservice.model.Crop;
import com.cropdeal.orderservice.model.Order;
import com.cropdeal.orderservice.model.Receipt;
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

//	private static final String INVENTORY_SERVICE_URL = "http://inventory-service";
	private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";
	private static final String PAYMENT_GATEWAY_URL = "https://api.paymentgateway.com";

	

	public List<Order> getAllOrders() {
		return repository.findAll();
	}

	public Order getOrderById(String id) throws InvalidOrderException {
		return repository.findById(id).orElseThrow(() -> new InvalidOrderException("Invalid order ID: " + id));
	}

	public Receipt placeOrderFromCart(String dealerId) throws InvalidCropException, CartNotFoundException, PaymentNotDoneException {
        // Retrieve the dealer's cart
        Cart cart = cartService.getCartByDealerId(dealerId);

        // Create a new order
        Order order = new Order();
        order.setDealerId(dealerId);
        order.setOrderItems(cart.getCartItems());

        // Calculate the prices
        double totalPrice = calculateTotalPrice(order.getOrderItems());

        // Create the receipt
        Receipt receipt = new Receipt();
        receipt.setOrderId(order.getOrderId());
        receipt.setDealerId(dealerId);
        receipt.setOrderItems(order.getOrderItems());
        receipt.setTotalPrice(totalPrice);
        receipt.setStatus("Placed");

        // Clear the dealer's cart
        cartService.clearCart(dealerId);

        // Process payment
        processPayment(receipt);

        // Save the order to the database
        createOrder(order);

        return receipt;
    }

    public Receipt placeOrderDirectly(String dealerId, List<Crop> orderItems) throws InvalidCropException, PaymentNotDoneException {
        // Create a new order
        Order order = new Order();
        order.setDealerId(dealerId);
        order.setOrderItems(orderItems);

        Order placedOrder = createOrder(order);

        // Create the receipt
        Receipt receipt = new Receipt();
        receipt.setOrderId(placedOrder.getOrderId());
        receipt.setDealerId(dealerId);
        receipt.setOrderItems(placedOrder.getOrderItems());
        receipt.setTotalPrice(calculateTotalPrice(placedOrder.getOrderItems()));
        receipt.setStatus("Placed");

        // Process payment
        processPayment(receipt);
        
        // Save the order to the database
        createOrder(order);

        // Save the order to the database
        return receipt;
    }

	public Order createOrder(Order order) throws PaymentNotDoneException {
	    boolean isPaymentDone = checkPaymentStatus(order);

	    if (isPaymentDone) {
	        for (Crop orderItem : order.getOrderItems()) {
	            updateInventory(orderItem.getId(), orderItem.getQuantity());
	        }

	        // Save the order to the database
	        return repository.save(order);
	    } else {
	        // Handle the case when payment is not done
	        // You can throw an exception, return null, or take any appropriate action based on your requirement
	        throw new PaymentNotDoneException("Payment is not done for the order");
	    }
	}
	
	private boolean checkPaymentStatus(Order order) {
	    // Connect to the payment gateway API to check the payment status
	    // You can use the PAYMENT_GATEWAY_URL and appropriate API endpoints to check the payment status
	    // Implement your logic to check if the payment is done or not
	    // Return true if payment is done, false otherwise
	    // Example:
	    // String paymentStatus = restTemplate.getForObject(PAYMENT_GATEWAY_URL + "/orders/" + order.getOrderId() + "/paymentStatus", String.class);
	    // return "SUCCESS".equals(paymentStatus);
	    return true; // Placeholder for the payment check logic
	}
	
	
	
	 public void cancelOrder(String orderId) throws InvalidOrderException, InvalidReceiptException {
	        Order order = getOrderById(orderId);
	        Receipt receipt = receiptService.getReceiptByOrderId(orderId);

	        // Process payment refund if the order has been paid
//	        if (receipt.getStatus().equals("Paid")) {
//	            processPaymentRefund(order);
//	        }

	        // Update inventory for each order item
	        for (Crop orderItem : order.getOrderItems()) {
	            updateInventory(orderItem.getId(), -1 * orderItem.getQuantity());
	        }

	        repository.delete(order);
	    }

	    public Order updateOrder(String orderId, Order updatedOrder) throws InvalidOrderException, InvalidReceiptException {
	        Order order = getOrderById(orderId);
	        Receipt receipt = receiptService.getReceiptByOrderId(orderId);

	        // Process payment adjustment if the order has been paid
	        if (receipt.getStatus().equals("Paid")) {
	            processPaymentAdjustment(order, updatedOrder);
	        }

	        order.setOrderItems(updatedOrder.getOrderItems());
	        return repository.save(order);
	    }

	    private void processPayment(Receipt receipt) {
//	        // Assuming you have a payment gateway client library or SDK
//
//	        // Prepare payment request object with necessary details
//	        PaymentRequest paymentRequest = new PaymentRequest();
//	        paymentRequest.setOrderId(receipt.getOrderId());
//	        paymentRequest.setAmount(receipt.getTotalPrice());
//	        // Set other payment details as required
//
//	        // Send payment request to the payment gateway API
//	        PaymentGatewayResponse paymentGatewayResponse = paymentGatewayClient.processPayment(paymentRequest);
//
//	        // Handle the payment gateway response
//	        if (paymentGatewayResponse.isSuccess()) {
//	            // Payment is successful
//	            receipt.setPaymentStatus("Paid");
//	            receipt.setTransactionId(paymentGatewayResponse.getTransactionId());
//	        } else {
//	            // Payment failed
//	            throw new PaymentProcessingException("Payment processing failed. Reason: " + paymentGatewayResponse.getError());
//	        }
//
//	        // Update the receipt in the repository
//	        repository.save(receipt);
	    }


	    private void processPaymentAdjustment(Order oldOrder, Order updatedOrder) {
	        // Connect to the payment gateway API to process the payment adjustment
	        // Send the necessary payment details, such as the order ID, original amount, and adjusted amount, to the payment gateway
	        // Handle the payment gateway response and update the order/payment status accordingly
	        // You may need to make HTTP requests to the payment gateway's API endpoints

	        // Example code snippet:
//	        PaymentGatewayResponse adjustmentResponse = restTemplate.postForObject(PAYMENT_GATEWAY_URL + "/adjustments", updatedOrder, PaymentGatewayResponse.class);
//	        if (adjustmentResponse != null && adjustmentResponse.isSuccess()) {
//	            // Update the order/payment status in the database if required
//	        } else {
//	            // Handle the case when payment adjustment fails
//	        }
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
}
