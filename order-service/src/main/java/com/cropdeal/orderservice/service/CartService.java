package com.cropdeal.orderservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;

import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidCropException;
import com.cropdeal.orderservice.model.Cart;
import com.cropdeal.orderservice.model.OrderItem;
import com.cropdeal.orderservice.repository.CartRepository;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;

//    @Autowired
//    private RestTemplate restTemplate;
//    
////    private static final String INVENTORY_SERVICE_URL = "http://inventory-service";
//    private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";

	public Cart getCartByDealerId(String dealerId) throws CartNotFoundException {
		return cartRepository.findByDealerId(dealerId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for dealer: " + dealerId));
	}

	public Cart addToCart(String dealerId, String cropId, int quantity) {

		Cart cart = cartRepository.findByDealerId(dealerId).orElse(null);
		if (cart == null) {
			cart = new Cart();
			cart.setDealerId(dealerId);
		}

		OrderItem cartItem = new OrderItem(cropId, quantity);
		cart.getCartItems().add(cartItem);

		return cartRepository.save(cart);
	}

	public Cart updateCart(String dealerId, String cropId, int quantity)
			throws CartNotFoundException, InvalidCropException {

		Cart cart = getCartByDealerId(dealerId);
		OrderItem cartItem = cart.getCartItemByCropId(cropId);
		if (cartItem == null) {
			cartItem = new OrderItem(cropId, quantity);
			cart.getCartItems().add(cartItem);
		} else {
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
		}
		return cartRepository.save(cart);
	}

	public Cart removeCartItem(String dealerId, String cropId) throws CartNotFoundException, InvalidCropException {
		Cart cart = getCartByDealerId(dealerId);
		if (cart == null) {

		}

		OrderItem cartItem = cart.getCartItemByCropId(cropId);
		if (cartItem == null) {
			throw new InvalidCropException("Crop not found in cart: " + cropId);
		}

		cart.getCartItems().remove(cartItem);

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
