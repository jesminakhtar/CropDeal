package com.cropdeal.inventoryservice.entity;

import java.util.List;

//Product.java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

	public Product() {
	}

	
	public Product(String name, String category, int quantity, double price, String description,
			String farmerId) {
		super();
		this.name = name;
		this.category = category;
		this.quantity = quantity;
		this.price = price;
		this.description = description;
		this.farmerId = farmerId;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}


	public String getFarmerId() {
		return farmerId;
	}


	public void setFarmerId(String farmerId) {
		this.farmerId = farmerId;
	}
}