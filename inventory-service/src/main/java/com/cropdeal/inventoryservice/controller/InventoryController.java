package com.cropdeal.inventoryservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyAuthority('Admin', 'SCOPE_internal')")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = inventoryService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasAnyAuthority('Admin', 'SCOPE_internal')")
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        try {
            Product product = inventoryService.getProductById(productId);
            return ResponseEntity.ok(product);
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyAuthority('Admin', 'Farmer')")
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = inventoryService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PreAuthorize("hasAnyAuthority('Admin', 'Farmer')")
    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable String productId, @RequestBody Product updatedProduct) {
        try {
            inventoryService.updateProduct(productId, updatedProduct);
            return ResponseEntity.ok("Product updated successfully.");
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyAuthority('Admin', 'Farmer')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId) {
        try {
            inventoryService.deleteProduct(productId);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyAuthority('Admin', 'Dealer')")
    @PostMapping("/{productId}/ratings")
    public ResponseEntity<String> addRating(@PathVariable String productId, @RequestBody Rating rating) {
        try {
            inventoryService.addRating(productId, rating);
            return ResponseEntity.status(HttpStatus.CREATED).body("Rating added successfully.");
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admins can add ratings.");
        }
    }

    @PreAuthorize("hasAnyAuthority('Admin', 'SCOPE_internal')")
    @PutMapping("/{productId}/updateQuantity")
    public ResponseEntity<String> updateProductQuantity(@PathVariable String productId, @RequestParam int quantity) {
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
