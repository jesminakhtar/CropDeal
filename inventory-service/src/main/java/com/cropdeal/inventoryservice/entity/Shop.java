package com.cropdeal.inventoryservice.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shops")
public class Shop {
	@Id
    private String id;
    private String name;
    private String farmerUsername;
    private byte[] imageData;
    private List<Product> products =  new ArrayList<>();
    private List<Rating> ratings = new ArrayList<>();
    
}
