package com.cropdeal.orderservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidCropException;
import com.cropdeal.orderservice.model.Cart;
import com.cropdeal.orderservice.model.Crop;
import com.cropdeal.orderservice.repository.CartRepository;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private RestTemplate restTemplate;

//	private static final String INVENTORY_SERVICE_URL = "http://inventory-service";
	private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";

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

		ResponseEntity<Crop> response = restTemplate.getForEntity(INVENTORY_SERVICE_URL + "/crops/" + cropId,
				Crop.class);
		Crop crop = response.getBody();
		crop.setQuantity(quantity);
		cart.getCartItems().add(crop);

		return cartRepository.save(cart);
	}

	public Cart updateCart(String dealerId, String cropId, int quantity)
			throws CartNotFoundException, InvalidCropException {

		Cart cart = getCartByDealerId(dealerId);

		ResponseEntity<Crop> response = restTemplate.getForEntity(INVENTORY_SERVICE_URL + "/crops/" + cropId,
				Crop.class);
		Crop cartItem = response.getBody();
		cartItem.setQuantity(cartItem.getQuantity() + quantity);
		return cartRepository.save(cart);
	}

	public Cart removeCartItem(String dealerId, String cropId) throws CartNotFoundException, InvalidCropException {
		Cart cart = getCartByDealerId(dealerId);
		ResponseEntity<Crop> response = restTemplate.getForEntity(INVENTORY_SERVICE_URL + "/crops/" + cropId,
				Crop.class);
		Crop cartItem = response.getBody();

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
