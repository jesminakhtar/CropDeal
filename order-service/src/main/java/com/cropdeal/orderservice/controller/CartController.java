package com.cropdeal.orderservice.controller;

import java.util.List;

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

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.service.CartService;

@RestController
@RequestMapping("/carts")
public class CartController {

	@Autowired
	private CartService cartService;
	
	@GetMapping("/all") 
	public List<Cart> getAllCarts() {
	   return cartService.getAllCarts();
	}
	
	@GetMapping("/{dealerId}") 
	public Cart getCartByDealerId(@PathVariable String dealerId) throws CartNotFoundException {
	   return cartService.getCartByDealerId(dealerId);
	}
	

	@PostMapping("/{dealerId}/items")
	public ResponseEntity<String> addToCart(@PathVariable String dealerId, @RequestParam String cropId,
			@RequestParam int quantity) throws InvalidProductException, CartNotFoundException {
		cartService.addToCart(dealerId, cropId, quantity);
		return ResponseEntity.status(HttpStatus.OK).body("Crop added to cart successfully.");
	}

	@PutMapping("/{dealerId}/items/{cropId}")
	public ResponseEntity<String> updateCartItemQuantity(@PathVariable String dealerId, @PathVariable String cropId,
			@RequestParam int quantity) throws CartNotFoundException, InvalidProductException {
		cartService.updateCartItemQuantity(dealerId, cropId, quantity);
		return ResponseEntity.status(HttpStatus.OK).body("Cart item quantity updated successfully.");

	}

	@DeleteMapping("/{dealerId}/items/{cropId}")
	public ResponseEntity<String> removeCartItem(@PathVariable String dealerId, @PathVariable String cropId)
			throws CartNotFoundException, InvalidProductException {

		cartService.removeCartItem(dealerId, cropId);
		return ResponseEntity.status(HttpStatus.OK).body("Cart item removed successfully.");
	}

	@DeleteMapping("/{dealerId}")
	public ResponseEntity<String> clearCart(@PathVariable String dealerId) throws CartNotFoundException {
		cartService.clearCart(dealerId);
		return ResponseEntity.status(HttpStatus.OK).body("Cart cleared successfully.");
	}
}
