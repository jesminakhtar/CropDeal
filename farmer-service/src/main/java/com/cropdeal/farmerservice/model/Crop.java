package com.cropdeal.farmerservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "crops")
public class Crop {
    @Id
    private String id;
    private String farmerId;
    private String name;
    private double quantity;
    private double price;
 
    public Crop() {
    }

    public Crop(String farmerId, String name, double quantity, double price) {
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

