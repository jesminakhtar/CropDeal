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

import com.cropdeal.farmerservice.enity.Farmer;
import com.cropdeal.farmerservice.exception.InvalidFarmerException;
import com.cropdeal.farmerservice.model.Crop;
import com.cropdeal.farmerservice.service.FarmerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/farmer")
public class FarmerController {

    @Autowired
    private FarmerService service;

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
        service.addFarmer(farmer);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
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

    @PostMapping("/{farmerId}/crops")
    public ResponseEntity<String> addCrop(@PathVariable String farmerId, @RequestBody Crop crop) throws InvalidFarmerException {
        service.addCrop(farmerId, crop);
        return ResponseEntity.status(HttpStatus.CREATED).body("Crop added successfully");
    }

    @PutMapping("/{farmerId}/crops/{cropId}")
    public ResponseEntity<String> updateCrop(@PathVariable String farmerId, @PathVariable String cropId, @Valid @RequestBody Crop crop)
            throws InvalidFarmerException {
        service.updateCrop(farmerId, cropId, crop);
        return ResponseEntity.status(HttpStatus.OK).body("Crop updated successfully");
    }

    @DeleteMapping("/{farmerId}/crops/{cropId}")
    public ResponseEntity<String> deleteCrop(@PathVariable String farmerId, @PathVariable String cropId) throws InvalidFarmerException {
        service.deleteCrop(farmerId, cropId);
        return ResponseEntity.status(HttpStatus.OK).body("Crop deleted successfully");
    }
}
