package com.cropdeal.orderservice.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.exception.TransactionNotFoundException;
import com.cropdeal.orderservice.service.OrderService;
import com.razorpay.RazorpayException;

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
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    public Order getOrderById(@PathVariable String id) throws InvalidOrderException {
        logger.info("Fetching order with ID: {}", id);
        return orderService.getOrderById(id);
    }
    
    @GetMapping("/user/{id}")
//  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
  public List<Order> getOrderByUserId(@PathVariable String id) {
      logger.info("Fetching orders for user: {}", id);
      List<Order> orders = orderService.getOrderByDealerId(id);
      logger.info("Orders found: {}", orders);
      return orders;
  }

    @PostMapping("/place-order/{dealerId}/{addressId}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    public ResponseEntity<Order> placeOrder(@PathVariable String dealerId, @PathVariable String addressId) throws CartNotFoundException, NoSuchAlgorithmException {
        logger.info("Placing order from cart for dealer");
        Order order = orderService.placeOrder(dealerId, addressId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PostMapping("/create-order/{orderId}/{paymentId}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    public ResponseEntity<Receipt> createOrder(@PathVariable String orderId ,@PathVariable String paymentId) throws PaymentNotDoneException, InvalidOrderException, RazorpayException {
        logger.info("Placing order for with ID: {} ", orderId);
        Receipt receipt = orderService.createOrder(orderId, paymentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }


    @DeleteMapping("/{orderId}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEALER')")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) throws InvalidOrderException, ReceiptNotFoundException, PaymentNotDoneException, TransactionNotFoundException {
        logger.info("Deleting order with ID: {}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body("Order cancelled successfully.");
    }
    
}
