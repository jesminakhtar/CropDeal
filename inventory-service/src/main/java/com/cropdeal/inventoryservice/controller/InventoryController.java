package com.cropdeal.inventoryservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cropdeal.inventoryservice.exception.InsufficientQuantityException;
import com.cropdeal.inventoryservice.exception.InvalidProductException;
import com.cropdeal.inventoryservice.exception.OutOfStockException;
import com.cropdeal.inventoryservice.entity.Product;
import com.cropdeal.inventoryservice.model.Rating;
import com.cropdeal.inventoryservice.service.InventoryService;

@RestController
@RequestMapping("/products")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = inventoryService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        try {
            Product product = inventoryService.getProductById(productId);
            return ResponseEntity.ok(product);
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addProduct(@RequestBody Product product, @RequestParam String authenticatedUserId) {
        inventoryService.addProduct(product, authenticatedUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added successfully.");
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable String productId, @RequestBody Product updatedProduct, @RequestParam String authenticatedUserId) {
        try {
            inventoryService.updateProduct(productId, updatedProduct, authenticatedUserId);
            return ResponseEntity.ok("Product updated successfully.");
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId, @RequestParam String authenticatedUserId) {
        try {
            inventoryService.deleteProduct(productId, authenticatedUserId);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{productId}/ratings")
    public ResponseEntity<String> addRating(@PathVariable String productId, @RequestBody Rating rating, @RequestParam String authenticatedUserId) {
        try {
            inventoryService.addRating(productId, rating, authenticatedUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Rating added successfully.");
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admins can add ratings.");
        }
    }

    @PutMapping("/{productId}/updateQuantity")
    public ResponseEntity<String> updateProductQuantity(@PathVariable String productId, @RequestBody int quantity) {
        try {
            inventoryService.updateProductQuantity(productId, quantity);
            return ResponseEntity.ok("Product quantity updated successfully.");
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        } catch (InsufficientQuantityException e) {
            return ResponseEntity.badRequest().body("Insufficient quantity.");
        } catch (OutOfStockException e) {
            return ResponseEntity.badRequest().body("Product is out of stock.");
        }
    }
}
