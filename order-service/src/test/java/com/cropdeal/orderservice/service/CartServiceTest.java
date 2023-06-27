package com.cropdeal.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("123");
    }

    @Test
    void getCartByDealerId_ExistingCart_CartReturned() throws CartNotFoundException {
        // Arrange
        String dealerId = "123";
        Cart cart = new Cart(dealerId, new HashMap<>());
        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.of(cart));

        // Act
        Cart result = cartService.getCartByDealerId(dealerId);

        // Assert
        assertNotNull(result);
        assertEquals(cart, result);
    }

    @Test
    void getCartByDealerId_NonExistingCart_CartNotFoundExceptionThrown() {
        // Arrange
        String dealerId = "123";
        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartNotFoundException.class, () -> cartService.getCartByDealerId(dealerId));
    }

    @Test
    void createCart_CartCreatedSuccessfully() {
        // Arrange
        String dealerId = "123";
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.createCart();

        // Assert
        assertNotNull(result);
        assertEquals(dealerId, result.getDealerId());
        assertTrue(result.getCartItems().isEmpty());
    }

    @Test
    void addToCart_ValidProduct_AddedToCartSuccessfully() throws InvalidProductException, CartNotFoundException {
        // Arrange
        String dealerId = "123";
        String productId = "456";
        int quantity = 2;
        Cart cart = new Cart(dealerId, new HashMap<>());
        Product product = new Product(productId, "Test Product", "Test Category", 10, 10.0, "Test Description", "Test Farmer", Collections.emptyList());
        ResponseEntity<Product> responseEntity = new ResponseEntity<>(product, HttpStatus.OK);

        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.of(cart));
        when(restTemplate.getForEntity("http://localhost:8082/products/findById/" + productId, Product.class))
                .thenReturn(responseEntity);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.addToCart(productId, quantity);

        // Assert
        assertNotNull(result);
        assertEquals(dealerId, result.getDealerId());
        assertEquals(quantity, result.getCartItems().get(productId));
    }

    @Test
    void addToCart_InvalidProduct_InvalidProductExceptionThrown() throws CartNotFoundException {
        // Arrange
        String dealerId = "123";
        String productId = "456";
        int quantity = 2;
        Cart cart = new Cart(dealerId, new HashMap<>());

        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.of(cart));
        when(restTemplate.getForEntity("http://localhost:8082/products/findById/" + productId, Product.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        // Act & Assert
        assertThrows(InvalidProductException.class, () -> cartService.addToCart(productId, quantity));
    }

    @Test
    void updateCartItemQuantity_ExistingProduct_QuantityUpdatedSuccessfully() throws CartNotFoundException, InvalidProductException {
        // Arrange
        String dealerId = "123";
        String productId = "456";
        int quantity = 5;
        Cart cart = new Cart(dealerId, new HashMap<>());
        cart.getCartItems().put(productId, 2);

        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.updateCartItemQuantity(productId, quantity);

        // Assert
        assertNotNull(result);
        assertEquals(dealerId, result.getDealerId());
        assertEquals(quantity, result.getCartItems().get(productId));
    }

    @Test
    void updateCartItemQuantity_NonExistingProduct_InvalidProductExceptionThrown() throws CartNotFoundException {
        // Arrange
        String dealerId = "123";
        String productId = "456";
        int quantity = 5;
        Cart cart = new Cart(dealerId, new HashMap<>());

        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(InvalidProductException.class, () -> cartService.updateCartItemQuantity(productId, quantity));
    }

    @Test
    void removeCartItem_ExistingProduct_RemovedFromCartSuccessfully() throws CartNotFoundException, InvalidProductException {
        // Arrange
        String dealerId = "123";
        String productId = "456";
        Cart cart = new Cart(dealerId, new HashMap<>());
        cart.getCartItems().put(productId, 2);

        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.removeCartItem(productId);

        // Assert
        assertNotNull(result);
        assertEquals(dealerId, result.getDealerId());
        assertFalse(result.getCartItems().containsKey(productId));
    }

    @Test
    void removeCartItem_NonExistingProduct_InvalidProductExceptionThrown() throws CartNotFoundException {
        // Arrange
        String dealerId = "123";
        String productId = "456";
        Cart cart = new Cart(dealerId, new HashMap<>());

        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(InvalidProductException.class, () -> cartService.removeCartItem(productId));
    }

    @Test
    void clearCart_ExistingCart_CartClearedSuccessfully() throws CartNotFoundException {
        // Arrange
        String dealerId = "123";
        Cart cart = new Cart(dealerId, new HashMap<>());

        when(cartRepository.findByDealerId(dealerId)).thenReturn(Optional.of(cart));

        // Act
        cartService.clearCart();

        // Assert
        verify(cartRepository, times(1)).delete(cart);
    }

    @Test
    void getAllCarts_CartsRetrievedSuccessfully() {
        // Arrange
        Cart cart = new Cart("123", new HashMap<>());
        when(cartRepository.findAll()).thenReturn(Collections.singletonList(cart));

        // Act
        List<Cart> result = cartService.getAllCarts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cart, result.get(0));
    }
}
