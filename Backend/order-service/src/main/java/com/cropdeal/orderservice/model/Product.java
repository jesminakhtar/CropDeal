package com.cropdeal.orderservice.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	private String id;
	private String name;
	private String category;
	private int quantity;
	private double price;
	private String description;
	private String shopId;
	private byte[] imageData;
	private List<Rating> ratings;
}