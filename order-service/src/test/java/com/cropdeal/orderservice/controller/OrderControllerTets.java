package com.cropdeal.orderservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.service.OrderService;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllOrders() {
        // Mock the behavior of the orderService.getAllOrders() method
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderService.getAllOrders()).thenReturn(orders);

        // Call the controller method
        List<Order> result = orderController.getAllOrders();

        // Verify the result
        assertEquals(2, result.size());
        assertEquals(orders.get(0), result.get(0));
        assertEquals(orders.get(1), result.get(1));
    }

    @Test
    void testGetOrderById() throws InvalidOrderException {
        // Mock the behavior of the orderService.getOrderById() method
        String orderId = "123";
        Order order = new Order();
        when(orderService.getOrderById(orderId)).thenReturn(order);

        // Call the controller method
        Order result = orderController.getOrderById(orderId);

        // Verify the result
        assertEquals(order, result);
    }

    @Test
    void testPlaceOrderFromCart() throws InvalidOrderException, CartNotFoundException, PaymentNotDoneException {
        // Mock the behavior of the orderService.placeOrderFromCart() method
        String dealerId = "456";
        Receipt receipt = new Receipt();
        when(orderService.placeOrderFromCart(dealerId)).thenReturn(receipt);

        // Call the controller method
        ResponseEntity<Receipt> response = orderController.placeOrder(dealerId);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(receipt, response.getBody());
    }

    @Test
    void testPlaceOrderDirectly() throws PaymentNotDoneException, InvalidProductException {
        // Mock the behavior of the orderService.placeOrderDirectly() method
        String dealerId = "789";
        String cropId = "111";
        int quantity = 2;
        Receipt receipt = new Receipt();
        when(orderService.placeOrderDirectly(dealerId, cropId, quantity)).thenReturn(receipt);

        // Call the controller method
        ResponseEntity<Receipt> response = orderController.createOrder(dealerId, cropId, quantity);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(receipt, response.getBody());
    }

    @Test
    void testUpdateOrder() throws InvalidOrderException, PaymentNotDoneException, ReceiptNotFoundException {
        // Mock the behavior of the orderService.updateOrder() method
        String dealerId = "999";
        Order order = new Order();
        // Simulate the order update
        doNothing().when(orderService).updateOrder(eq(dealerId), any(Order.class));

        // Call the controller method
        ResponseEntity<String> response = orderController.updateOrder(dealerId, order);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order updated successfully.", response.getBody());
    }

    @Test
    void testDeleteOrder() throws InvalidOrderException, ReceiptNotFoundException, PaymentNotDoneException {
        // Mock the behavior of the orderService.cancelOrder() method
        String orderId = "555";
        // Simulate the order cancellation
        doNothing().when(orderService).cancelOrder(orderId);

        // Call the controller method
        ResponseEntity<String> response = orderController.deleteOrder(orderId);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order cancelled successfully.", response.getBody());
    }
}
