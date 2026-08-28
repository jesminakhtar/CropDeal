package com.cropdeal.inventoryservice.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cropdeal.inventoryservice.entity.Product;
import com.cropdeal.inventoryservice.entity.Rating;
import com.cropdeal.inventoryservice.exception.InsufficientQuantityException;
import com.cropdeal.inventoryservice.exception.InvalidProductException;
import com.cropdeal.inventoryservice.exception.OutOfStockException;
import com.cropdeal.inventoryservice.exception.ShopNotFoundException;
import com.cropdeal.inventoryservice.service.InventoryService;

@RestController
@RequestMapping("/products")
public class InventoryController {
	
	Logger log = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;    
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
    	log.info("Fetching all products");
        List<Product> products = inventoryService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/findById/{productId}")
//    @PreAuthorize("permitAll()")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        try {
            Product product = inventoryService.getProductById(productId);
            return ResponseEntity.ok(product);
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/add")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FARMER')")
    public ResponseEntity<Product> addProduct(@RequestParam("file") MultipartFile file,
                                              @RequestParam("name") String name,
                                              @RequestParam("shopId") String shopId,
                                              @RequestParam("category") String category,
                                              @RequestParam("quantity") int quantity,
                                              @RequestParam("price") double price,
                                              @RequestParam("description") String description) throws ShopNotFoundException {
        try {
            // Read the image file and convert it to byte[]
            byte[] imageData = file.getBytes();

            // Create a new Product instance with the image data
            Product product = new Product(name, category, quantity, price, description, shopId, imageData);
            Product savedProduct = inventoryService.addProduct(product);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FARMER')")
    public ResponseEntity<String> updateProduct(@PathVariable String productId, @RequestBody Product updatedProduct) {
        try {
            inventoryService.updateProduct(productId, updatedProduct);
            return ResponseEntity.ok("Product updated successfully.");
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{productId}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FARMER')")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId) throws ShopNotFoundException {
        try {
            inventoryService.deleteProduct(productId);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (InvalidProductException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{productId}/ratings")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
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

    @PutMapping("/{productId}/updateQuantity")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    public ResponseEntity<String> updateProductQuantity(@PathVariable String productId, @RequestParam int quantity) {
        try {
        	log.info("Updating product with id {} quantity {}", productId, quantity);
            inventoryService.updateProductQuantity(productId, quantity);
            log.info("Product quantity updated successfully.");
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
