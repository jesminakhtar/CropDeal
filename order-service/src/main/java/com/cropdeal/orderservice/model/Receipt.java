package com.cropdeal.orderservice.model;

import java.math.BigDecimal;
import java.util.List;

public class Receipt {
    private String orderId;
    private String dealerId;
    private List<Crop> orderItems;
    private double totalPrice;
    private String status;
    // Add other necessary properties

    public Receipt() {
    }

    public Receipt(String orderId, String dealerId, List<Crop> orderItems, double totalPrice, String status) {
        this.orderId = orderId;
        this.dealerId = dealerId;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        this.status = status;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
