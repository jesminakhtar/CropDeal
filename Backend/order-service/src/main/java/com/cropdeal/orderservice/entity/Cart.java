package com.cropdeal.orderservice.entity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    private String id;
    private String dealerId;
    private double totalPrice; 
    private Map<String, Integer> cartItems = new HashMap<>();
    
    
}
