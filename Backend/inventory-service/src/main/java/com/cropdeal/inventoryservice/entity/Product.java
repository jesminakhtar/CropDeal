package com.cropdeal.inventoryservice.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Document(collection = "products")
public class Product {

	@Id
    private String id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private String description;
    private String shopId;
    private byte[] imageData;
    private List<Rating> ratings;
    
    public Product() {
		this.ratings = new ArrayList<>();
	}

	public Product(String name, String category, int quantity, double price, String description, String shopId,
			byte[] imageData) {
		super();
		this.name = name;
		this.category = category;
		this.quantity = quantity;
		this.price = price;
		this.description = description;
		this.shopId = shopId;
		this.ratings = new ArrayList<>();
		this.imageData = imageData;
	}
    
	
    
    
}
