package com.cropdeal.inventoryservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropdeal.inventoryservice.exception.InsufficientQuantityException;
import com.cropdeal.inventoryservice.exception.InvalidProductException;
import com.cropdeal.inventoryservice.exception.OutOfStockException;
import com.cropdeal.inventoryservice.entity.Product;
import com.cropdeal.inventoryservice.model.Rating;
import com.cropdeal.inventoryservice.repository.InventoryRepository;

@Service
public class InventoryService {

	@Autowired
	private InventoryRepository repository;

	public List<Product> getAllProducts() {
		return repository.findAll();
	}

	public Product getProductById(String id) throws InvalidProductException {
		return repository.findById(id).orElseThrow(() -> new InvalidProductException("Invalid product ID: " + id));
	}

	public void addProduct(Product product, String authenticatedUserId) {
		// Check if the authenticated user is a farmer or admin
		if (!isFarmerOrAdmin(authenticatedUserId)) {
			throw new IllegalArgumentException("Only farmers or admins can add products");
		}

		repository.save(product);
	}

	public void updateProduct(String id, Product updatedProduct, String authenticatedUserId) throws InvalidProductException {
		Product product = getProductById(id);

		// Check if the authenticated user is a farmer or admin
		if (!isFarmerOrAdmin(authenticatedUserId)) {
			throw new IllegalArgumentException("Only farmers or admins can update products");
		}

		product.setName(updatedProduct.getName());
		product.setQuantity(updatedProduct.getQuantity());
		product.setPrice(updatedProduct.getPrice());
		repository.save(product);
	}

	public void deleteProduct(String id, String authenticatedUserId) throws InvalidProductException {
		Product product = getProductById(id);

		// Check if the authenticated user is a farmer or admin
		if (!isFarmerOrAdmin(authenticatedUserId)) {
			throw new IllegalArgumentException("Only farmers or admins can delete products");
		}

		repository.delete(product);
	}

	public void updateProductQuantity(String id, int quantity)
			throws InvalidProductException, InsufficientQuantityException, OutOfStockException {
		Product product = getProductById(id);
		double prevQuantity = product.getQuantity();

		if (quantity == 0) {
			throw new OutOfStockException("Product " + id + " is currently out of stock");
		}
		if (quantity > prevQuantity) {
			throw new InsufficientQuantityException("Requested quantity, " + quantity + " exceeds the available quantity, " + prevQuantity + " for product " + id);
		}

		product.setQuantity(product.getQuantity() - quantity);
		repository.save(product);
	}

	public void addRating(String productId, Rating rating, String authenticatedUserId) throws InvalidProductException {
		Product product = getProductById(productId);

		// Check if the authenticated user is an admin
		if (!isDealerOrAdmin(authenticatedUserId)) {
			throw new IllegalArgumentException("Only admins can add ratings");
		}

		product.getRatings().add(rating);
		repository.save(product);
	}

	private boolean isFarmerOrAdmin(String userId) {
		// Implement your farmer and admin authentication logic here
		// Return true if the user is a farmer or admin, otherwise false
		// You can use your authentication mechanism or roles to determine if the user is a farmer or admin
		// Example: return userService.isFarmerOrAdmin(userId);
		return true; // Change this based on your authentication logic
	}

	private boolean isDealerOrAdmin(String userId) {
		// Implement your admin authentication logic here
		// Return true if the user is an admin, otherwise false
		// You can use your authentication mechanism or roles to determine if the user is an admin
		// Example: return userService.isAdmin(userId);
		return true; // Change this based on your authentication logic
	}
}
