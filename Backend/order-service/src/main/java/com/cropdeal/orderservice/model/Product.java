package com.cropdeal.orderservice.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {
	private String id;
	private String name;
	private String category;
	private int quantity;
	private double price;
	private String description;
	private String farmerId;
	private List<Rating> ratings;
	
}
