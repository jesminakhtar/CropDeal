package com.cropdeal.inventoryservice.controller;


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

import com.cropdeal.inventoryservice.exception.InsufficientQuantityException;
import com.cropdeal.inventoryservice.exception.InvalidCropException;
import com.cropdeal.inventoryservice.exception.OutOfStockException;
import com.cropdeal.inventoryservice.model.Crop;
import com.cropdeal.inventoryservice.service.InventoryService;

@RestController
@RequestMapping("/crops")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/all")
    public List<Crop> getAllCrops() {
        return inventoryService.getAllCrops();
    }

    @GetMapping("/{id}")
    public Crop getCropById(@PathVariable String cropId) throws InvalidCropException {
        return inventoryService.getCropById(cropId);
    }

    @PostMapping
    public ResponseEntity<String> addCrop(@RequestBody Crop crop) {
        inventoryService.addCrop(crop);
        return ResponseEntity.status(HttpStatus.CREATED).body("Crop added successfully.");
    }

    @PutMapping("/{cropId}")
    public ResponseEntity<String> updateCrop(@PathVariable String cropId, @RequestBody Crop crop)
            throws InvalidCropException {
        inventoryService.updateCrop(cropId, crop);
        return ResponseEntity.status(HttpStatus.OK).body("Crop updated successfully.");
    }

    @DeleteMapping("/{cropId}")
    public ResponseEntity<String> deleteCrop(@PathVariable String cropId) throws InvalidCropException {
        inventoryService.deleteCrop(cropId);
        return ResponseEntity.status(HttpStatus.OK).body("Crop deleted successfully.");
    }
    
    
    @PutMapping("/{cropId}/updateQuantity")
    public void updateCropQuantity(@PathVariable String cropId, @RequestBody int quantity) throws InvalidCropException, InsufficientQuantityException, OutOfStockException {
    	
    	inventoryService.updateCropQuantity(cropId, quantity);
    }
    
}
