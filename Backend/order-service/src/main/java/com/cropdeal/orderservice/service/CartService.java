package com.cropdeal.orderservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.exception.OutOfStockException;
import com.cropdeal.orderservice.model.Product;
import com.cropdeal.orderservice.repository.CartRepository;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RestTemplate restTemplate;


    private static final String INVENTORY_SERVICE_URL = "http://inventory-service";

    public Cart getCartByDealerId(String dId) throws CartNotFoundException {
        log.info("Fetching cart for dealer: {}", dId);
        return cartRepository.findByDealerId(dId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for dealer: " + dId));
    }

    public Cart createCart(String dealerId) {
        log.info("Creating cart for dealer: {}", dealerId);
        Cart cart = new Cart();
        cart.setDealerId(dealerId);
        cart.setCartItems(new HashMap<String, Integer>());
        cart.setTotalPrice(0);
        return cartRepository.save(cart);
    }

    public Cart addToCart(String dealerId, String productId, int quantity) throws InvalidProductException, CartNotFoundException, OutOfStockException {
        log.info("Adding product {} with quantity {} to cart for dealer: {}", productId, quantity, dealerId);
        Cart cart = cartRepository.findByDealerId(dealerId).orElse(null);

        if (cart == null) {
            cart = createCart(dealerId);
        }

        Product product = getProductByRestTemplate(productId);

        log.info("Product : {}", product);

        if (product == null) {
            throw new InvalidProductException("Invalid product ID: " + productId);
        }

        if (product.getQuantity() < quantity) {
            throw new OutOfStockException("Product " + productId + " is out of stock");
        }

        Map<String, Integer> cartItems = cart.getCartItems();
        log.info("CartItems : {}", cartItems);

        cartItems.put(productId, quantity);

        double totalPrice = calculateTotalPrice(cartItems);
        cart.setTotalPrice(totalPrice);

        log.info(
                "Total immediately before save: {}",
                cart.getTotalPrice()
        );

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
        cart.setTotalPrice(calculateTotalPrice(cartItems));

        return cartRepository.save(cart);
    }

    public Cart removeCartItem(String dealerId,String productId) throws CartNotFoundException, InvalidProductException {
    	log.info("Removing product {} from cart for dealer: {}", productId, dealerId);
        Cart cart = getCartByDealerId(dealerId);
        Map<String, Integer> cartItems = cart.getCartItems();

        if (!cartItems.containsKey(productId)) {
            throw new InvalidProductException("Product not found in cart: " + productId);
        }

        cartItems.remove(productId);
        cart.setTotalPrice(calculateTotalPrice(cartItems));

        return cartRepository.save(cart);
    }
    
    public double getTotalPrice(String dealerId) throws CartNotFoundException {
    	Cart cart = getCartByDealerId(dealerId);
    	
    	Map<String,Integer> orderItems = cart.getCartItems();
    	double totalPrice = 0;
		for (Map.Entry<String, Integer> orderItemEntry : orderItems.entrySet()) {
			String productId = orderItemEntry.getKey();
			int quantity = orderItemEntry.getValue();
			Product product = getProductByRestTemplate(productId);
			double itemPrice = product.getPrice() * quantity;
			totalPrice += itemPrice;
			
		}
		return totalPrice;
    	 
    }

    private double calculateTotalPrice(
            Map<String, Integer> cartItems) {

        double totalPrice = 0.0;

        for (Map.Entry<String, Integer> entry
                : cartItems.entrySet()) {

            String productId = entry.getKey();
            int quantity = entry.getValue();

            Product product =
                    getProductByRestTemplate(productId);

            log.info(
                    "CART CALC -> productId={}, product={}, price={}, quantity={}",
                    productId,
                    product.getName(),
                    product.getPrice(),
                    quantity
            );

            double itemPrice =
                    product.getPrice() * quantity;

            totalPrice += itemPrice;

            log.info(
                    "CART CALC -> itemPrice={}, runningTotal={}",
                    itemPrice,
                    totalPrice
            );
        }

        log.info(
                "CART CALC -> FINAL TOTAL={}",
                totalPrice
        );

        return totalPrice;
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
    
    public String generateUniqueCartId() {
		String uniqueId = UUID.randomUUID().toString();
		return uniqueId.replace("-", "").substring(0, 6);
	}
    
    public Product getProductByRestTemplate(String productId) {
    	ResponseEntity<Product> response = restTemplate.getForEntity(INVENTORY_SERVICE_URL + "/products/findById/" + productId,
                Product.class);
        return response.getBody();
    }

}
