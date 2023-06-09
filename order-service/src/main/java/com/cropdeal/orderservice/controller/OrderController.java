package com.cropdeal.orderservice.controller;

import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidCropException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.InvalidReceiptException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.model.Order;
import com.cropdeal.orderservice.model.Receipt;
import com.cropdeal.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable String id) throws InvalidOrderException {
        return orderService.getOrderById(id);
    }

    @PostMapping("/place-order/{dealerId}")
    public ResponseEntity<String> placeOrder(@PathVariable String dealerId) throws InvalidOrderException, InvalidCropException, CartNotFoundException, PaymentNotDoneException {
        Receipt receipt = orderService.placeOrderFromCart(dealerId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully. " + receipt);
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) throws PaymentNotDoneException {
        orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrder(@PathVariable String id, @RequestBody Order order) throws InvalidOrderException, InvalidReceiptException {
        orderService.updateOrder(id, order);
        return ResponseEntity.status(HttpStatus.OK).body("Order updated successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) throws InvalidOrderException, InvalidReceiptException {
        orderService.cancelOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body("Order cancelled successfully.");
    }
}
