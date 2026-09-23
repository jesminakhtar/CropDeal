package com.cropdeal.orderservice.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.orderservice.dto.FarmerOrderResponse;
import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.service.FarmerOrderService;
import com.cropdeal.orderservice.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final FarmerOrderService farmerOrderService;

    public OrderController(OrderService orderService, FarmerOrderService farmerOrderService) {
        this.orderService = orderService;
        this.farmerOrderService = farmerOrderService;
    }

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        return orderService.getAllOrders();
    }

    @GetMapping("/farmer/{username}")
    public List<FarmerOrderResponse> getFarmerOrders(@PathVariable String username) {
        logger.info("Fetching sales orders for farmer {}", username);
        return farmerOrderService.getFarmerOrders(username);
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable String id) throws InvalidOrderException {
        logger.info("Fetching order with ID {}", id);
        return orderService.getOrderById(id);
    }

    @GetMapping("/user/{id}")
    public List<Order> getOrderByUserId(@PathVariable String id) {
        logger.info("Fetching orders for user {}", id);
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

    @GetMapping("/farmer/{username}/{orderId}")
    public FarmerOrderResponse getFarmerOrder(
            @PathVariable String username,
            @PathVariable String orderId
    ) {
        logger.info("Fetching order {} for farmer {}", orderId, username);
        return farmerOrderService.getFarmerOrder(username, orderId);
    }
}