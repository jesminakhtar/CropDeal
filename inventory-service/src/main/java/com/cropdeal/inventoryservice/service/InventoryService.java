package com.cropdeal.inventoryservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropdeal.inventoryservice.entity.Product;
import com.cropdeal.inventoryservice.entity.Rating;
import com.cropdeal.inventoryservice.exception.InsufficientQuantityException;
import com.cropdeal.inventoryservice.exception.InvalidProductException;
import com.cropdeal.inventoryservice.exception.OutOfStockException;
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

	public Product addProduct(Product product) {

		return repository.save(product);
	}

	public void updateProduct(String id, Product updatedProduct) throws InvalidProductException {
		Product product = getProductById(id);

		product.setName(updatedProduct.getName());
		product.setQuantity(updatedProduct.getQuantity());
		product.setPrice(updatedProduct.getPrice());
		repository.save(product);
	}

	public void deleteProduct(String id) throws InvalidProductException {
		Product product = getProductById(id);
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

	public void addRating(String productId, Rating rating) throws InvalidProductException {
		Product product = getProductById(productId);
		product.getRatings().add(rating);
		repository.save(product);
	}
}
