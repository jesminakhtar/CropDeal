package com.cropdeal.orderservice.controller;

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

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.service.CartService;

@RestController
@RequestMapping("/carts")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @GetMapping("/all")
    public List<Cart> getAllCarts() {
        logger.info("Fetching all carts");
        return cartService.getAllCarts();
    }

    @GetMapping("/{dealerId}")
    public Cart getCartByDealerId(@PathVariable String dealerId) throws CartNotFoundException {
        logger.info("Fetching cart by dealer ID: {}", dealerId);
        return cartService.getCartByDealerId(dealerId);
    }

    @PostMapping("/{dealerId}/create")
    public ResponseEntity<String> createCart(@PathVariable String dealerId) {
        logger.info("Creating cart for dealer ID: {}", dealerId);
        cartService.createCart(dealerId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Cart created successfully.");
    }

    @PostMapping("/{dealerId}/items")
    public ResponseEntity<String> addToCart(@PathVariable String dealerId, @RequestParam String cropId,
                                            @RequestParam int quantity) throws InvalidProductException, CartNotFoundException {
        logger.info("Adding crop to cart. Dealer ID: {}, Crop ID: {}, Quantity: {}", dealerId, cropId, quantity);
        cartService.addToCart(dealerId, cropId, quantity);
        return ResponseEntity.status(HttpStatus.OK).body("Crop added to cart successfully.");
    }

    @PutMapping("/{dealerId}/items/{cropId}")
    public ResponseEntity<String> updateCartItemQuantity(@PathVariable String dealerId, @PathVariable String cropId,
                                                         @RequestParam int quantity) throws CartNotFoundException, InvalidProductException {
        logger.info("Updating cart item quantity. Dealer ID: {}, Crop ID: {}, Quantity: {}", dealerId, cropId, quantity);
        cartService.updateCartItemQuantity(dealerId, cropId, quantity);
        return ResponseEntity.status(HttpStatus.OK).body("Cart item quantity updated successfully.");
    }

    @DeleteMapping("/{dealerId}/items/{cropId}")
    public ResponseEntity<String> removeCartItem(@PathVariable String dealerId, @PathVariable String cropId)
            throws CartNotFoundException, InvalidProductException {
        logger.info("Removing cart item. Dealer ID: {}, Crop ID: {}", dealerId, cropId);
        cartService.removeCartItem(dealerId, cropId);
        return ResponseEntity.status(HttpStatus.OK).body("Cart item removed successfully.");
    }

    @DeleteMapping("/{dealerId}")
    public ResponseEntity<String> clearCart(@PathVariable String dealerId) throws CartNotFoundException {
        logger.info("Clearing cart for dealer ID: {}", dealerId);
        cartService.clearCart(dealerId);
        return ResponseEntity.status(HttpStatus.OK).body("Cart cleared successfully.");
    }
}
