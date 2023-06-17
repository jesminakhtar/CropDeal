package com.cropdeal.inventoryservice.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {

    @Id
    private String id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private String description;
    private String farmerId;
    private List<Rating> ratings;
    private String imageUrl;
}
