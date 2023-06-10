package com.cropdeal.orderservice.service;

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.model.Product;
import com.cropdeal.orderservice.repository.CartRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";

    public Cart getCartByDealerId(String dealerId) throws CartNotFoundException {
        return cartRepository.findByDealerId(dealerId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for dealer: " + dealerId));
    }

    public Cart createCart(String dealerId) {
        Cart cart = new Cart(dealerId, new HashMap<>());
        return cartRepository.save(cart);
    }

    public Cart addToCart(String dealerId, String productId, int quantity) throws InvalidProductException, CartNotFoundException {
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
        Cart cart = getCartByDealerId(dealerId);
        Map<String, Integer> cartItems = cart.getCartItems();

        if (!cartItems.containsKey(productId)) {
            throw new InvalidProductException("Product not found in cart: " + productId);
        }

        cartItems.put(productId, quantity);

        return cartRepository.save(cart);
    }

    public Cart removeCartItem(String dealerId, String productId) throws CartNotFoundException, InvalidProductException {
        Cart cart = getCartByDealerId(dealerId);
        Map<String, Integer> cartItems = cart.getCartItems();

        if (!cartItems.containsKey(productId)) {
            throw new InvalidProductException("Product not found in cart: " + productId);
        }

        cartItems.remove(productId);

        return cartRepository.save(cart);
    }

    public void clearCart(String dealerId) throws CartNotFoundException {
        Cart cart = getCartByDealerId(dealerId);
        if (cart != null) {
            cartRepository.delete(cart);
        }
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }
}
