package com.cropdeal.orderservice.entity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "carts")
public class Cart {

    @Id
    private String id;
    private String dealerId;
    private Map<String, Integer> cartItems = new HashMap<>();
    
    public Cart() {}
    
    public Cart(String dealerId, Map<String, Integer> cartItems) {
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
    
    public Map<String, Integer> getCartItems() {
        return cartItems;
    }
    
    public void setCartItems(Map<String, Integer> cartItems) {
        this.cartItems = cartItems;
    }
}
