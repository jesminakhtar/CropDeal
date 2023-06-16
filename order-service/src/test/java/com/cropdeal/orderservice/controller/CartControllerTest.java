package com.cropdeal.orderservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.service.CartService;

class CartControllerTest {
    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCarts() {
        // Mock the cartService.getAllCarts() method
        List<Cart> expectedCarts = List.of(new Cart("dealer1", new HashMap<>()), new Cart("dealer2", new HashMap<>()));
        when(cartService.getAllCarts()).thenReturn(expectedCarts);

        // Call the controller method
        List<Cart> actualCarts = cartController.getAllCarts();

        // Verify the result
        assertEquals(expectedCarts, actualCarts);
        verify(cartService, times(1)).getAllCarts();
    }

    @Test
    void testGetCartByDealerId() throws CartNotFoundException {
        // Mock the cartService.getCartByDealerId() method
        Cart expectedCart = new Cart("dealer1", new HashMap<>());
        when(cartService.getCartByDealerId("dealer1")).thenReturn(expectedCart);

        // Call the controller method
        Cart actualCart = cartController.getCartByDealerId("dealer1");

        // Verify the result
        assertEquals(expectedCart, actualCart);
        verify(cartService, times(1)).getCartByDealerId("dealer1");
    }

    @Test
    void testCreateCart() {
        // Call the controller method
        ResponseEntity<String> responseEntity = cartController.createCart("dealer1");

        // Verify the result
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Cart created successfully.", responseEntity.getBody());
        verify(cartService, times(1)).createCart("dealer1");
    }

    @Test
    void testAddToCart() throws InvalidProductException, CartNotFoundException {
        // Call the controller method
        ResponseEntity<String> responseEntity = cartController.addToCart("dealer1", "crop1", 5);

        // Verify the result
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Crop added to cart successfully.", responseEntity.getBody());
        verify(cartService, times(1)).addToCart("dealer1", "crop1", 5);
    }

    @Test
    void testUpdateCartItemQuantity() throws InvalidProductException, CartNotFoundException {
        // Call the controller method
        ResponseEntity<String> responseEntity = cartController.updateCartItemQuantity("dealer1", "crop1", 10);

        // Verify the result
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Cart item quantity updated successfully.", responseEntity.getBody());
        verify(cartService, times(1)).updateCartItemQuantity("dealer1", "crop1", 10);
    }

    @Test
    void testRemoveCartItem() throws InvalidProductException, CartNotFoundException {
        // Call the controller method
        ResponseEntity<String> responseEntity = cartController.removeCartItem("dealer1", "crop1");

        // Verify the result
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Cart item removed successfully.", responseEntity.getBody());
        verify(cartService, times(1)).removeCartItem("dealer1", "crop1");
    }

    @Test
    void testClearCart() throws CartNotFoundException {
        // Call the controller method
        ResponseEntity<String> responseEntity = cartController.clearCart("dealer1");

        // Verify the result
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Cart cleared successfully.", responseEntity.getBody());
        verify(cartService, times(1)).clearCart("dealer1");
    }
}
