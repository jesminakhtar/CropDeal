package com.cropdeal.farmerservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.farmerservice.entity.Farmer;
import com.cropdeal.farmerservice.exception.InvalidFarmerException;
import com.cropdeal.farmerservice.model.Product;
import com.cropdeal.farmerservice.service.FarmerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/farmer")
public class FarmerController {

    @Autowired
    private FarmerService service;

    //Farmer
    
    @GetMapping("/all")
    public List<Farmer> getAllFarmers() {
        return service.getAllFarmers();
    }

    @GetMapping("/{id}")
    public Farmer getFarmerById(@PathVariable String id) throws InvalidFarmerException {
        return service.getFarmerById(id);
    }

    @PostMapping
    public ResponseEntity<String> addFarmer(@RequestBody Farmer farmer) {
        Farmer savedFarmer = service.addFarmer(farmer);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful. Farmer id : " + savedFarmer.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateFarmer(@PathVariable String id, @Valid @RequestBody Farmer farmer)
            throws InvalidFarmerException {
        service.updateFarmer(id, farmer);
        return ResponseEntity.status(HttpStatus.OK).body("Updation successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFarmer(@PathVariable String id) throws InvalidFarmerException {
        service.deleteFarmer(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deletion successful");
    }

    
    // Products
    
    @PostMapping("/{farmerId}/product")
    public ResponseEntity<String> addProduct(@PathVariable String farmerId, @RequestBody Product product) throws InvalidFarmerException {
        service.addProduct(farmerId, product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added successfully");
    }

    @PutMapping("/{farmerId}/product/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable String farmerId, @PathVariable String productId, @Valid @RequestBody Product product)
            throws InvalidFarmerException {
        service.updateProduct(farmerId, productId, product);
        return ResponseEntity.status(HttpStatus.OK).body("Product updated successfully");
    }

    @DeleteMapping("/{farmerId}/product/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String farmerId, @PathVariable String productId) throws InvalidFarmerException {
        service.deleteProduct(farmerId, productId);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
    }
}
