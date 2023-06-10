package com.cropdeal.orderservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.cropdeal.orderservice.exception.InvalidCropException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.InvalidReceiptException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.service.OrderService;

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

    @PostMapping("/place-order-cart/{dealerId}")
    public ResponseEntity<Receipt> placeOrder(@PathVariable String dealerId) throws InvalidOrderException, CartNotFoundException, PaymentNotDoneException {
        Receipt receipt = orderService.placeOrderFromCart(dealerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }

    @PostMapping("/place-order/{dealerId}/{cropId}/{quantity}")
    public ResponseEntity<Receipt> createOrder(@PathVariable String dealerId, @PathVariable String cropId, @PathVariable int quantity) throws PaymentNotDoneException, InvalidCropException {
    	Receipt receipt = orderService.placeOrderDirectly(dealerId, cropId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }

    @PutMapping("/{dealerId}")
    public ResponseEntity<String> updateOrder(@PathVariable String dealerId, @RequestBody Order order) throws InvalidOrderException, InvalidReceiptException, PaymentNotDoneException {
        orderService.updateOrder(dealerId, order);
        return ResponseEntity.status(HttpStatus.OK).body("Order updated successfully.");
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) throws InvalidOrderException, InvalidReceiptException {
        orderService.cancelOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body("Order cancelled successfully.");
    }
}
