package com.cropdeal.orderservice.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
public class Order {
	@Id
    private String orderId;
    private String dealerId;
    private List<Crop> orderItems;

    public Order() {
    }

    public Order(String dealerId, List<Crop> orderItems) {
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

    public List<Crop> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Crop> orderItems) {
        this.orderItems = orderItems;
    }
    
}
