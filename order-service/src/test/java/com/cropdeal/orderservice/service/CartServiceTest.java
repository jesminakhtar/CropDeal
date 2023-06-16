package com.cropdeal.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.model.Product;
import com.cropdeal.orderservice.repository.CartRepository;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCartByDealerId_ExistingCart() throws CartNotFoundException {
        // Prepare data
        String dealerId = "123";
        Cart cart = new Cart(dealerId, new HashMap<>());

        // Mock the cartRepository.findByDealerId() method
        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.of(cart));

        // Call the service method
        Cart result = cartService.getCartByDealerId(dealerId);

        // Verify the result
        assertNotNull(result);
        assertEquals(cart, result);
        verify(cartRepository, times(1)).findByDealerId(dealerId);
    }

    @Test
    void testGetCartByDealerId_CartNotFound() {
        // Prepare data
        String dealerId = "123";

        // Mock the cartRepository.findByDealerId() method
        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.empty());

        // Call the service method and verify the exception
        assertThrows(CartNotFoundException.class, () -> cartService.getCartByDealerId(dealerId));
        verify(cartRepository, times(1)).findByDealerId(dealerId);
    }

    @Test
    void testCreateCart() {
        // Prepare data
        String dealerId = "123";
        Cart cart = new Cart(dealerId, new HashMap<>());

        // Mock the cartRepository.save() method
        when(cartRepository.save(cart)).thenReturn(cart);

        // Call the service method
        Cart result = cartService.createCart(dealerId);

        // Verify the result
        assertNotNull(result);
        assertEquals(cart, result);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testAddToCart_ValidProduct() throws InvalidProductException, CartNotFoundException {
        // Prepare data
        String dealerId = "123";
        String productId = "456";
        int quantity = 2;

        Cart cart = new Cart(dealerId, new HashMap<>());
        Product product = new Product(productId, "Product 1", "Category", quantity, 10.0, "Description", "Farmer ID", null);

        // Mock the cartService.getCartByDealerId() method
        when(cartService.getCartByDealerId(dealerId)).thenReturn(cart);

        // Mock the restTemplate.getForEntity() method
        ResponseEntity<Product> responseEntity = new ResponseEntity<>(product, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Product.class))).thenReturn(responseEntity);

        // Call the service method
        Cart result = cartService.addToCart(dealerId, productId, quantity);

        // Verify the result
        assertNotNull(result);
        assertEquals(cart, result);
        assertTrue(cart.getCartItems().containsKey(productId));
        assertEquals(quantity, cart.getCartItems().get(productId));
        verify(cartService, times(1)).getCartByDealerId(dealerId);
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Product.class));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testAddToCart_InvalidProduct() throws CartNotFoundException {
        // Prepare data
        String dealerId = "123";
        String productId = "456";
        int quantity = 2;

        Cart cart = new Cart(dealerId, new HashMap<>());

        // Mock the cartService.getCartByDealerId() method
        when(cartService.getCartByDealerId(dealerId)).thenReturn(cart);

        // Mock the restTemplate.getForEntity() method to return null product
        ResponseEntity<Product> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Product.class))).thenReturn(responseEntity);

        // Call the service method and verify the exception
        assertThrows(InvalidProductException.class, () -> cartService.addToCart(dealerId, productId, quantity));
        verify(cartService, times(1)).getCartByDealerId(dealerId);
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Product.class));
        verify(cartRepository, never()).save(cart);
    }
}
