package com.cropdeal.orderservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.InvalidProductException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    public Order getOrderById(@PathVariable String id) throws InvalidOrderException {
        logger.info("Fetching order with ID: {}", id);
        return orderService.getOrderById(id);
    }

    @PostMapping("/place-order-cart")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    public ResponseEntity<Receipt> placeOrder() throws InvalidOrderException, CartNotFoundException, PaymentNotDoneException {
        logger.info("Placing order from cart for dealer");
        Receipt receipt = orderService.placeOrderFromCart();
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }

    @PostMapping("/place-order/{productId}/{quantity}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    public ResponseEntity<Receipt> createOrder(@PathVariable String productId, @PathVariable int quantity) throws PaymentNotDoneException, InvalidProductException {
        logger.info("Placing direct order for dealer for crop with ID: {} and quantity: {}", productId, quantity);
        Receipt receipt = orderService.placeOrderDirectly(productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'DEALER')")
    public ResponseEntity<String> updateOrder(@PathVariable String orderId, @RequestBody Order order) throws InvalidOrderException, PaymentNotDoneException, ReceiptNotFoundException {
        logger.info("Updating order with ID: {}", orderId);
        orderService.updateOrder(orderId, order);
        return ResponseEntity.status(HttpStatus.OK).body("Order updated successfully.");
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) throws InvalidOrderException, ReceiptNotFoundException, PaymentNotDoneException {
        logger.info("Deleting order with ID: {}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body("Order cancelled successfully.");
    }
    
}
