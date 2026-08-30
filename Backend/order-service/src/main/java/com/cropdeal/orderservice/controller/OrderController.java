package com.cropdeal.orderservice.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable String id) throws InvalidOrderException {
        return orderService.getOrderById(id);
    }

    @GetMapping("/user/{id}")
    public List<Order> getOrderByUserId(@PathVariable String id) {
        return orderService.getOrderByDealerId(id);
    }

    @PostMapping("/place-order/{dealerId}/{addressId}")
    public ResponseEntity<Order> placeOrder(@PathVariable String dealerId, @PathVariable String addressId) throws CartNotFoundException, NoSuchAlgorithmException {
        Order order = orderService.placeOrder(dealerId, addressId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) throws InvalidOrderException {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully.");
    }
}