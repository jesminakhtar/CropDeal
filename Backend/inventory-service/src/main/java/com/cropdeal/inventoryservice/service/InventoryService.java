package com.cropdeal.inventoryservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cropdeal.inventoryservice.entity.Product;
import com.cropdeal.inventoryservice.entity.Rating;
import com.cropdeal.inventoryservice.entity.Shop;
import com.cropdeal.inventoryservice.exception.InsufficientQuantityException;
import com.cropdeal.inventoryservice.exception.InvalidProductException;
import com.cropdeal.inventoryservice.exception.OutOfStockException;
import com.cropdeal.inventoryservice.exception.ShopNotFoundException;
import com.cropdeal.inventoryservice.repository.InventoryRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryService {

	@Autowired
	private InventoryRepository repository;
	
	@Autowired
	private ShopService shopService;
	
	public List<Product> getAllProducts() {
		return repository.findAll();
	}

	public Product getProductById(String id) throws InvalidProductException {
		return repository.findById(id).orElseThrow(() -> new InvalidProductException("Invalid product ID: " + id));
	}

	public Product addProduct(Product product) throws ShopNotFoundException {
		String shopId = product.getShopId();
		Shop shop = shopService.getShopById(shopId);
		Product productOptional = repository.findByShopIdAndName(shopId, product.getName()).orElse(null);
		if (productOptional != null) {
			throw new IllegalArgumentException(product.getName() + "already exists in" + shopId);
		} else {
			
			product.setShopId(shopId);
			String category = product.getCategory();
			product.setId(category.substring(0,1) + generateUniqueId());
			
            List<Product> products = shop.getProducts();
            products.add(product);
            
            shop.setProducts(products);
            
            shopService.addShop(shop);
			
			return repository.save(product);
		}
	}

	public void updateProduct(String id, Product updatedProduct) throws InvalidProductException {
		Product product = getProductById(id);

		product.setName(updatedProduct.getName());
		product.setQuantity(updatedProduct.getQuantity());
		product.setPrice(updatedProduct.getPrice());
		repository.save(product);
	}

	public void deleteProduct(String id) throws InvalidProductException, ShopNotFoundException {
		Product product = getProductById(id);
		
		Shop shop = shopService.getShopById(product.getShopId());
		shop.getProducts().remove(product);
		
		shopService.addShop(shop);
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
			throw new InsufficientQuantityException("Requested quantity, " + quantity
					+ " exceeds the available quantity, " + prevQuantity + " for product " + id);
		}

		product.setQuantity(product.getQuantity() - quantity);
		repository.save(product);
	}

	public void addRating(String productId, Rating rating) throws InvalidProductException {
		String dealerId = retrieveUserId();
		rating.setDealerId(dealerId);
		Product product = getProductById(productId);
		product.getRatings().add(rating);
		repository.save(product);
	}

	public String retrieveUserId() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info("Userid retrieve : {}", username);
		return username;
	}
	
	private String generateUniqueId() {
	    String uniqueId = UUID.randomUUID().toString();
	    return uniqueId.replace("-", "").substring(0,6);
	}

}
