package com.cropdeal.inventoryservice.entity;

import java.util.List;

//Product.java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cropdeal.inventoryservice.model.Rating;

@Document(collection = "products")
public class Product {
	@Id
	private String id;
	private String name;
	private String category;
	private int quantity;
	private double price;
	private String description;
	private List<Rating> ratings;

	public Product() {
	}

	public Product(String id, String name, String category, int quantity, double price, String description,
			List<Rating> ratings) {
		this.name = name;
		this.category = category;
		this.quantity = quantity;
		this.price = price;
		this.description = description;
		this.ratings = ratings;
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

}
