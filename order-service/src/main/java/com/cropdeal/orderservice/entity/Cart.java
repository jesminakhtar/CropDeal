package com.cropdeal.orderservice.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cropdeal.orderservice.model.Crop;

@Document(collection = "carts")
public class Cart {

    @Id
    private String id;
    private String dealerId;
    private List<Crop> cartItems = new ArrayList<>();
    
	public Cart() {}
	
	public Cart(String dealerId, List<Crop> cartItems) {
		this.dealerId = dealerId;
		this.cartItems = cartItems;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDealerId() {
		return dealerId;
	}
	public void setDealerId(String dealerId) {
		this.dealerId = dealerId;
	}
	public List<Crop> getCartItems() {
		return cartItems;
	}
	public void setCartItems(List<Crop> cartItems) {
		this.cartItems = cartItems;
	}
	
	public Crop getCartItemByCropId(String cropId) {
	    for (Crop orderItem : this.getCartItems()) {
	        if (orderItem.getId().equals(cropId)) {
	            return orderItem;
	        }
	    }
	    return null;
	}
    
}

