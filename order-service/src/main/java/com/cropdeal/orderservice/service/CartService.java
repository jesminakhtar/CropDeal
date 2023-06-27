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
import org.springframework.security.core.context.SecurityContextHolder;
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

    public Cart getCartByDealerId(String dId) throws CartNotFoundException {
        log.info("Fetching cart for dealer: {}", dId);
        return cartRepository.findByDealerId(dId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for dealer: " + dId));
    }

    public Cart createCart() {
    	String dealerId = retrieveUserId();
        log.info("Creating cart for dealer: {}", dealerId);
        Cart cart = new Cart(dealerId, new HashMap<>());
        return cartRepository.save(cart);
    }

    public Cart addToCart(String productId, int quantity) throws InvalidProductException, CartNotFoundException {
    	String dealerId = retrieveUserId();
    	log.info("Adding product {} with quantity {} to cart for dealer: {}", productId, quantity, dealerId);
        Cart cart = getCartByDealerId(dealerId);

        ResponseEntity<Product> response = restTemplate.getForEntity(INVENTORY_SERVICE_URL + "/products/findById/" + productId,
                Product.class);
        Product product = response.getBody();
        
        log.info("Product : {}" , product);
        
        if (product == null) {
            throw new InvalidProductException("Invalid product ID: " + productId);
        }
        
        Map<String, Integer> cartItems = cart.getCartItems();
        log.info("CartItems : {}" , cartItems);
        
        cartItems.put(productId, quantity);
        
        log.info("Added product in cart : {}" , cartItems);

        return cartRepository.save(cart);
    }

    public Cart updateCartItemQuantity(String productId, int quantity)
            throws CartNotFoundException, InvalidProductException {
    	String dealerId = retrieveUserId();
        log.info("Updating quantity of product {} to {} in cart for dealer: {}", productId, quantity, dealerId);
        Cart cart = getCartByDealerId(dealerId);
        Map<String, Integer> cartItems = cart.getCartItems();

        if (!cartItems.containsKey(productId)) {
            throw new InvalidProductException("Product not found in cart: " + productId);
        }

        cartItems.put(productId, quantity);

        return cartRepository.save(cart);
    }

    public Cart removeCartItem(String productId) throws CartNotFoundException, InvalidProductException {
    	String dealerId = retrieveUserId();
    	log.info("Removing product {} from cart for dealer: {}", productId, dealerId);
        Cart cart = getCartByDealerId(dealerId);
        Map<String, Integer> cartItems = cart.getCartItems();

        if (!cartItems.containsKey(productId)) {
            throw new InvalidProductException("Product not found in cart: " + productId);
        }

        cartItems.remove(productId);

        return cartRepository.save(cart);
    }

    public void clearCart() throws CartNotFoundException {
    	String dealerId = retrieveUserId();
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
    
    private String retrieveUserId() {
		String id = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info("Userid retrieve : {}", id);
		return id;
	}
}
