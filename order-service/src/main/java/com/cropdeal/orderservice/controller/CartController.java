package com.cropdeal.orderservice.controller;

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

    Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @GetMapping("/all")
    public List<Cart> getAllCarts() {
        logger.info("Fetching all carts");
        return cartService.getAllCarts();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    @GetMapping("/{dealerId}")
    public Cart getCartByDealerId(@PathVariable String dealerId) throws CartNotFoundException {
        logger.info("Fetching cart by dealer ID: {}", dealerId);
        return cartService.getCartByDealerId(dealerId);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    @PostMapping("/create")
    public ResponseEntity<String> createCart() {
        logger.info("Creating cart");
        cartService.createCart();
        return ResponseEntity.status(HttpStatus.CREATED).body("Cart created successfully.");
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    @PostMapping("/items")
    public ResponseEntity<String> addToCart(@RequestParam String cropId,
                                            @RequestParam int quantity) throws InvalidProductException, CartNotFoundException {
        logger.info("Adding crop to cart. Crop ID: {}, Quantity: {}",cropId, quantity);
        cartService.addToCart(cropId, quantity);
        return ResponseEntity.status(HttpStatus.OK).body("Crop added to cart successfully.");
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    @PutMapping("/items/{cropId}")
    public ResponseEntity<String> updateCartItemQuantity(@PathVariable String cropId,
                                                         @RequestParam int quantity) throws CartNotFoundException, InvalidProductException {
        logger.info("Updating cart item quantity. Crop ID: {}, Quantity: {}", cropId, quantity);
        cartService.updateCartItemQuantity(cropId, quantity);
        return ResponseEntity.status(HttpStatus.OK).body("Cart item quantity updated successfully.");
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    @DeleteMapping("/items/{cropId}")
    public ResponseEntity<String> removeCartItem(@PathVariable String cropId)
            throws CartNotFoundException, InvalidProductException {
        logger.info("Removing cart item. Crop ID: {}", cropId);
        cartService.removeCartItem(cropId);
        return ResponseEntity.status(HttpStatus.OK).body("Cart item removed successfully.");
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    @DeleteMapping
    public ResponseEntity<String> clearCart() throws CartNotFoundException {
        logger.info("Clearing cart");
        cartService.clearCart();
        return ResponseEntity.status(HttpStatus.OK).body("Cart cleared successfully.");
    }
}
