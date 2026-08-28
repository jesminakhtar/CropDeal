package com.cropdeal.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.orderservice.entity.Cart;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.repository.CartRepository;

@SpringBootTest
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCartByDealerId() throws CartNotFoundException {
        // Arrange
        String dealerId = "dealer1";
        Cart expectedCart = new Cart();
        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.of(expectedCart));

        // Act
        Cart actualCart = cartService.getCartByDealerId(dealerId);

        // Assert
        assertEquals(expectedCart, actualCart);
    }

    @Test
    void testCreateCart() {
        // Arrange
        String dealerId = "dealer1";
        Cart expectedCart = new Cart();
        when(cartRepository.save(any(Cart.class))).thenReturn(expectedCart);

        // Act
        Cart actualCart = cartService.createCart(dealerId);

        // Assert
        assertEquals(expectedCart, actualCart);
    }

}
