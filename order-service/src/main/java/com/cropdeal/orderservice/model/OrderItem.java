package com.cropdeal.orderservice.model;

public class OrderItem {
    private String cropId;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(String cropId, int quantity) {
        this.cropId = cropId;
        this.quantity = quantity;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
}
