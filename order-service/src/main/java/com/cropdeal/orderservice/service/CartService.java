package com.cropdeal.orderservice.service;

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.model.Product;
import com.cropdeal.orderservice.repository.CartRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";

    public Cart getCartByDealerId(String dealerId) throws CartNotFoundException {
        log.info("Fetching cart for dealer: {}", dealerId);
        return cartRepository.findByDealerId(dealerId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for dealer: " + dealerId));
    }

    public Cart createCart(String dealerId) {
        log.info("Creating cart for dealer: {}", dealerId);
        Cart cart = new Cart(dealerId, new HashMap<>());
        return cartRepository.save(cart);
    }

    public Cart addToCart(String dealerId, String productId, int quantity) throws InvalidProductException, CartNotFoundException {
        log.info("Adding product {} with quantity {} to cart for dealer: {}", productId, quantity, dealerId);
        Cart cart = getCartByDealerId(dealerId);

        ResponseEntity<Product> response = restTemplate.getForEntity(INVENTORY_SERVICE_URL + "/products/" + productId,
                Product.class);
        Product product = response.getBody();
        if (product == null) {
            throw new InvalidProductException("Invalid product ID: " + productId);
        }

        Map<String, Integer> cartItems = cart.getCartItems();
        cartItems.put(productId, quantity);

        return cartRepository.save(cart);
    }

    public Cart updateCartItemQuantity(String dealerId, String productId, int quantity)
            throws CartNotFoundException, InvalidProductException {
        log.info("Updating quantity of product {} to {} in cart for dealer: {}", productId, quantity, dealerId);
        Cart cart = getCartByDealerId(dealerId);
        Map<String, Integer> cartItems = cart.getCartItems();

        if (!cartItems.containsKey(productId)) {
            throw new InvalidProductException("Product not found in cart: " + productId);
        }

        cartItems.put(productId, quantity);

        return cartRepository.save(cart);
    }

    public Cart removeCartItem(String dealerId, String productId) throws CartNotFoundException, InvalidProductException {
        log.info("Removing product {} from cart for dealer: {}", productId, dealerId);
        Cart cart = getCartByDealerId(dealerId);
        Map<String, Integer> cartItems = cart.getCartItems();

        if (!cartItems.containsKey(productId)) {
            throw new InvalidProductException("Product not found in cart: " + productId);
        }

        cartItems.remove(productId);

        return cartRepository.save(cart);
    }

    public void clearCart(String dealerId) throws CartNotFoundException {
        log.info("Clearing cart for dealer: {}", dealerId);
        Cart cart = getCartByDealerId(dealerId);
        if (cart != null) {
            cartRepository.delete(cart);
        }
    }

    public List<Cart> getAllCarts() {
        log.info("Fetching all carts");
        return cartRepository.findAll();
    }
}
