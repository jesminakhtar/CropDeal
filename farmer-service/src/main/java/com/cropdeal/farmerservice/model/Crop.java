package com.cropdeal.farmerservice.model;

public class Crop {
    private String id;
    private String farmerId;
    private String name;
    private int quantity;
    private double price;
 
    public Crop() {
    }

    public Crop(String farmerId, String name, int quantity, double price) {
        this.farmerId = farmerId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

