package com.cropdeal.orderservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "orders")
public class Order {
    @Id
    private String orderId;
    private String dealerId;
    private Map<String, Integer> orderItems;

    public Order() {
        this.orderItems = new HashMap<>();
    }

    public Order(String dealerId, Map<String, Integer> orderItems) {
        this.dealerId = dealerId;
        this.orderItems = orderItems;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public Map<String, Integer> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Map<String, Integer> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(String productId, int quantity) {
        orderItems.put(productId, quantity);
    }

    public void removeOrderItem(String productId) {
        orderItems.remove(productId);
    }
}
