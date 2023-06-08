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
import com.cropdeal.orderservice.model.Cart;
import com.cropdeal.orderservice.model.Order;
import com.cropdeal.orderservice.model.OrderItem;
import com.cropdeal.orderservice.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OrderRepository repository;

	@Autowired
	private CartService cartService;

//	private static final String INVENTORY_SERVICE_URL = "http://inventory-service";
	private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";

	public List<Order> getAllOrders() {
		return repository.findAll();
	}

	public Order getOrderById(String id) throws InvalidOrderException {
		return repository.findById(id).orElseThrow(() -> new InvalidOrderException("Invalid order ID: " + id));
	}

	public Order placeOrderFromCart(String dealerId) throws InvalidCropException, CartNotFoundException {
		// Retrieve the dealer's cart
		Cart cart = cartService.getCartByDealerId(dealerId);

		// Create a new order
		Order order = new Order();
		order.setDealerId(dealerId);
		order.setOrderItems(cart.getCartItems());

		// Clear the dealer's cart
		cartService.clearCart(dealerId);

		// Save the order to the database
		return createOrder(order);
	}

	public Order placeOrderDirectly(String dealerId, List<OrderItem> orderItems) throws InvalidCropException {
		// Create a new order
		Order order = new Order();
		order.setDealerId(dealerId);
		order.setOrderItems(orderItems);

		// Save the order to the database
		return createOrder(order);
	}
	
	public Order createOrder(Order order) {
		for (OrderItem orderItem : order.getOrderItems()) {
			updateInventory(orderItem.getCropId(), orderItem.getQuantity());
		}

        // Save the order to the database
        return repository.save(order);
    }

	public void cancelOrder(String orderId) throws InvalidOrderException {

		Order order = getOrderById(orderId);
		// Update inventory for each order item
		for (OrderItem orderItem : order.getOrderItems()) {
			updateInventory(orderItem.getCropId(), -1 * orderItem.getQuantity());
		}

		repository.delete(order);
	}

	public Order updateOrder(String id, Order updatedOrder) throws InvalidOrderException {
		Order order = getOrderById(id);
		order.setOrderItems(updatedOrder.getOrderItems());
		return repository.save(order);
	}

	private void updateInventory(String cropId, int quantity) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Integer> requestEntity = new HttpEntity<>(quantity, headers);

		String url = INVENTORY_SERVICE_URL + "/crops/" + cropId + "/updateQuantity";
		restTemplate.put(url, requestEntity);
	}
}
