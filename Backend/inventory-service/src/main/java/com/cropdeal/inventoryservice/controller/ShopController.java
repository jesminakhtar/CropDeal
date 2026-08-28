package com.cropdeal.inventoryservice.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cropdeal.inventoryservice.entity.Product;
import com.cropdeal.inventoryservice.entity.Shop;
import com.cropdeal.inventoryservice.exception.ShopNotFoundException;
import com.cropdeal.inventoryservice.exception.UserNotFoundException;
import com.cropdeal.inventoryservice.service.ShopService;

@RestController
@RequestMapping("/shops")
public class ShopController {
	private static final Logger logger = LoggerFactory.getLogger(ShopController.class);

	@Autowired
	private ShopService shopService;

	@GetMapping
	public ResponseEntity<List<Shop>> getAllShops() {
		logger.info("Fetching all shops");
		List<Shop> shops = shopService.getAllShops();
		logger.info("Total shops found: {}", shops.size());
		return ResponseEntity.ok(shops);
	}

	@GetMapping("/farmerUsername/{username}")
	public ResponseEntity<List<Shop>> getShopByFarmerUsername(@PathVariable String username)
			throws ShopNotFoundException, UserNotFoundException {
		logger.info("Fetching shop by farmer username: {}", username);
		List<Shop> shops = shopService.getShopByFarmerUsername(username);
		logger.info("Shops found: {}", shops);
		return ResponseEntity.ok(shops);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Shop> getShopById(@PathVariable String id)
			throws ShopNotFoundException {
		
		logger.info("Fetching shop by id: {}", id);
		Shop shop = shopService.getShopById(id);
		logger.info("Shop found: {}", shop);
		return ResponseEntity.ok(shop);
	}

	@PostMapping("/add")
	public ResponseEntity<Shop> addShop(@RequestParam("file") MultipartFile file, @RequestParam("name") String name,
			@RequestParam("farmerUsername") String farmerUsername) {

		try {
			logger.info("Adding new shop...}");
			
			byte[] imageData = file.getBytes();
			Shop shop = new Shop();
			shop.setName(name);
			shop.setId(farmerUsername);
			shop.setImageData(imageData);
			
			Shop addedShop = shopService.addShop(shop);
			
			logger.info("Shop added: {}", addedShop);
			return ResponseEntity.status(HttpStatus.CREATED).body(addedShop);
			
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	@PutMapping("/{shopId}")
	public ResponseEntity<Shop> updateShop(@PathVariable String shopId, @RequestParam("file") MultipartFile file,
			@RequestParam("name") String name, @RequestParam("farmerUsername") String farmerUsername)
			throws ShopNotFoundException {

		try {
			// Read the image file and convert it to byte[]
			byte[] imageData = file.getBytes();

			logger.info("Trying to update shop with id {}", shopId);
			Shop shop = new Shop();
			shop.setName(name);
			shop.setId(farmerUsername);
			shop.setImageData(imageData);
			
			Shop updatedShop = shopService.updateShop(shopId, shop);
			logger.info("Shop updated successfully");
			return ResponseEntity.status(HttpStatus.CREATED).body(updatedShop);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	@DeleteMapping("/{shopId}")
	public ResponseEntity<String> deleteShop(@PathVariable String shopId) {
		logger.info("Trying to delete shop with id {}", shopId);
		try {
			shopService.deleteShop(shopId);
			return ResponseEntity.ok("Shop with id" + shopId + "was deleted successfully!");
		} catch (ShopNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/getAllProducts/{shopId}")
	public ResponseEntity<List<Product>> getProductsByShopId(@PathVariable String shopId) throws ShopNotFoundException {
		logger.info("Fetching products for shop {}", shopId);
		List<Product> products = shopService.getProductsByShopId(shopId);
		logger.info("Products found : {}", products);
		return ResponseEntity.ok(products);
	}
	
	@GetMapping("/rating/{shopId}")
	public ResponseEntity<Double> getAverageRating(@PathVariable String shopId) throws ShopNotFoundException {
		logger.info("Calculating average rating for shop {}", shopId);
		double avgRating= shopService.getAverageRating(shopId);
		logger.info("Rating is : {}", avgRating);
		return ResponseEntity.ok(avgRating);
	}

}
