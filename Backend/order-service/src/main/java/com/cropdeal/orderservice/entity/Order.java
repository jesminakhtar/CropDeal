package com.cropdeal.orderservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "orders")
@Data
public class Order {
    

	@Id
    private String orderId;
    private String dealerId;
    private double totalPrice;
    private Map<String, Integer> orderItems;
    private String status; 
    private String deliveryAddressId;

    public Order(String dealerId, double totalPrice, Map<String, Integer> orderItems, String deliveryAddressId) {
		super();
		this.dealerId = dealerId;
		this.totalPrice = totalPrice;
		this.orderItems = orderItems;
		this.deliveryAddressId = deliveryAddressId;
	}
    
    public Order() {
        this.orderItems = new HashMap<>();
    }

    
    public void addOrderItem(String productId, int quantity) {
        orderItems.put(productId, quantity);
    }

    public void removeOrderItem(String productId) {
        orderItems.remove(productId);
    }
}
