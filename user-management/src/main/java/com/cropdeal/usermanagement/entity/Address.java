package com.cropdeal.usermanagement.entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "addresses")
public class Address {
    @Id
    private String id;
    
    @NotEmpty(message = "Name is required")
    private String name;
    
    @NotEmpty(message = "User ID is required")
    private String userId;
    
    @NotEmpty(message = "House number is required")
    private String houseNo;
    
    @NotEmpty(message = "Road name is required")
    private String roadName;
    
    private String landmark;
    
    @NotEmpty(message = "PIN code is required")
    private String pin;
    
    @NotEmpty(message = "City is required")
    private String city;
    
    @NotEmpty(message = "State is required")
    private String state;
    
    @NotEmpty(message = "Country is required")
    private String country;
    
    @Pattern(regexp = "home|work", message = "Type can only be 'home' or 'work'")
    private String type;
}
