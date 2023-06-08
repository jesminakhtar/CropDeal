package com.cropdeal.orderservice.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "carts")
public class Cart {

    @Id
    private String id;
    private String dealerId;
    private List<OrderItem> cartItems = new ArrayList<>();
    
	public Cart() {}
	
	public Cart(String dealerId, List<OrderItem> cartItems) {
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
	public List<OrderItem> getCartItems() {
		return cartItems;
	}
	public void setCartItems(List<OrderItem> cartItems) {
		this.cartItems = cartItems;
	}
	
	public OrderItem getCartItemByCropId(String cropId) {
	    for (OrderItem orderItem : this.getCartItems()) {
	        if (orderItem.getCropId().equals(cropId)) {
	            return orderItem;
	        }
	    }
	    return null;
	}
    
}

