package com.cropdeal.inventoryservice.controller;

import com.cropdeal.inventoryservice.entity.Product;
import com.cropdeal.inventoryservice.entity.Rating;
import com.cropdeal.inventoryservice.exception.InvalidProductException;
import com.cropdeal.inventoryservice.exception.InsufficientQuantityException;
import com.cropdeal.inventoryservice.exception.OutOfStockException;
import com.cropdeal.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        List<Product> products = new ArrayList<>();
        products.add(new Product("Product1", "Category1", 10, 100.0, "Description1", "image1.jpg"));
        products.add(new Product("Product2", "Category2", 20, 200.0, "Description2", "image2.jpg"));
        when(inventoryService.getAllProducts()).thenReturn(products);

        // Act
        ResponseEntity<List<Product>> response = inventoryController.getAllProducts();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(products, response.getBody());
    }

    @Test
    void getProductById_WithValidProductId_ShouldReturnProduct() throws InvalidProductException {
        // Arrange
        String productId = "123";
        Product product = new Product("Product1", "Category1", 10, 100.0, "Description1", "image1.jpg");
        when(inventoryService.getProductById(productId)).thenReturn(product);

        // Act
        ResponseEntity<Product> response = inventoryController.getProductById(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(product, response.getBody());
    }

    @Test
    void getProductById_WithInvalidProductId_ShouldReturnNotFound() throws InvalidProductException {
        // Arrange
        String productId = "123";
        when(inventoryService.getProductById(productId)).thenThrow(new InvalidProductException(""));

        // Act
        ResponseEntity<Product> response = inventoryController.getProductById(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void addProduct_WithValidProduct_ShouldReturnCreatedProduct() {
        // Arrange
        Product product = new Product("Product1", "Category1", 10, 100.0, "Description1", "image1.jpg");
        when(inventoryService.addProduct(any(Product.class))).thenReturn(product);

        // Act
        ResponseEntity<Product> response = inventoryController.addProduct(product);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(product, response.getBody());
    }

    @Test
    void updateProduct_WithValidProductIdAndProduct_ShouldReturnSuccessMessage() throws InvalidProductException {
        // Arrange
        String productId = "123";
        Product updatedProduct = new Product("Product1", "Category1", 10, 100.0, "Description1", "image1.jpg");

        // Act
        ResponseEntity<String> response = inventoryController.updateProduct(productId, updatedProduct);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product updated successfully.", response.getBody());
        verify(inventoryService, times(1)).updateProduct(eq(productId), any(Product.class));
    }

    @Test
    void updateProduct_WithInvalidProductId_ShouldReturnNotFound() throws InvalidProductException {
        // Arrange
        String productId = "123";
        Product updatedProduct = new Product("Product1", "Category1", 10, 100.0, "Description1", "image1.jpg");
        doThrow(new InvalidProductException("")).when(inventoryService).updateProduct(eq(productId), any(Product.class));

        // Act
        ResponseEntity<String> response = inventoryController.updateProduct(productId, updatedProduct);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(inventoryService, times(1)).updateProduct(eq(productId), any(Product.class));
    }

    @Test
    void deleteProduct_WithValidProductId_ShouldReturnSuccessMessage() throws InvalidProductException {
        // Arrange
        String productId = "123";

        // Act
        ResponseEntity<String> response = inventoryController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully.", response.getBody());
        verify(inventoryService, times(1)).deleteProduct(productId);
    }

    @Test
    void deleteProduct_WithInvalidProductId_ShouldReturnNotFound() throws InvalidProductException {
        // Arrange
        String productId = "123";
        doThrow(new InvalidProductException("")).when(inventoryService).deleteProduct(productId);

        // Act
        ResponseEntity<String> response = inventoryController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(inventoryService, times(1)).deleteProduct(productId);
    }

    @Test
    void addRating_WithValidProductIdAndRating_ShouldReturnCreatedMessage() throws InvalidProductException {
        // Arrange
        String productId = "123";
        Rating rating = new Rating("Dealer1", 4, "Good product");

        // Act
        ResponseEntity<String> response = inventoryController.addRating(productId, rating);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Rating added successfully.", response.getBody());
        verify(inventoryService, times(1)).addRating(eq(productId), any(Rating.class));
    }

    @Test
    void addRating_WithInvalidProductId_ShouldReturnNotFound() throws InvalidProductException {
        // Arrange
        String productId = "123";
        Rating rating = new Rating("Dealer1", 4, "Good product");
        doThrow(new InvalidProductException("")).when(inventoryService).addRating(eq(productId), any(Rating.class));

        // Act
        ResponseEntity<String> response = inventoryController.addRating(productId, rating);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(inventoryService, times(1)).addRating(eq(productId), any(Rating.class));
    }

    @Test
    void addRating_WithUnauthorizedUser_ShouldReturnUnauthorizedMessage() throws InvalidProductException {
        // Arrange
        String productId = "123";
        Rating rating = new Rating("Dealer1", 4, "Good product");
        doThrow(new IllegalArgumentException()).when(inventoryService).addRating(eq(productId), any(Rating.class));

        // Act
        ResponseEntity<String> response = inventoryController.addRating(productId, rating);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Only admins can add ratings.", response.getBody());
        verify(inventoryService, times(1)).addRating(eq(productId), any(Rating.class));
    }

    @Test
    void updateProductQuantity_WithValidProductIdAndQuantity_ShouldReturnSuccessMessage() throws InvalidProductException, InsufficientQuantityException, OutOfStockException {
        // Arrange
        String productId = "123";
        int quantity = 5;

        // Act
        ResponseEntity<String> response = inventoryController.updateProductQuantity(productId, quantity);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product quantity updated successfully.", response.getBody());
        verify(inventoryService, times(1)).updateProductQuantity(eq(productId), eq(quantity));
    }

    @Test
    void updateProductQuantity_WithInvalidProductId_ShouldReturnNotFound() throws InvalidProductException, InsufficientQuantityException, OutOfStockException {
        // Arrange
        String productId = "123";
        int quantity = 5;
        doThrow(new InvalidProductException("")).when(inventoryService).updateProductQuantity(eq(productId), eq(quantity));

        // Act
        ResponseEntity<String> response = inventoryController.updateProductQuantity(productId, quantity);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(inventoryService, times(1)).updateProductQuantity(eq(productId), eq(quantity));
    }

    @Test
    void updateProductQuantity_WithInsufficientQuantity_ShouldReturnBadRequest() throws InvalidProductException, InsufficientQuantityException, OutOfStockException {
        // Arrange
        String productId = "123";
        int quantity = 5;
        doThrow(new InsufficientQuantityException("")).when(inventoryService).updateProductQuantity(eq(productId), eq(quantity));

        // Act
        ResponseEntity<String> response = inventoryController.updateProductQuantity(productId, quantity);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Insufficient quantity.", response.getBody());
        verify(inventoryService, times(1)).updateProductQuantity(eq(productId), eq(quantity));
    }

    @Test
    void updateProductQuantity_WithOutOfStockProduct_ShouldReturnBadRequest() throws InvalidProductException, InsufficientQuantityException, OutOfStockException {
        // Arrange
        String productId = "123";
        int quantity = 5;
        doThrow(new OutOfStockException("")).when(inventoryService).updateProductQuantity(productId, quantity);

        // Act
        ResponseEntity<String> response = inventoryController.updateProductQuantity(productId, quantity);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product is out of stock.", response.getBody());
        verify(inventoryService, times(1)).updateProductQuantity(eq(productId), eq(quantity));
    }
}
