package com.cropdeal.orderservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.orderservice.dto.PaymentProcessingResponse;
import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.service.PaymentService;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public PaymentProcessingResponse initiatePayment(@RequestParam double amount) throws PaymentNotDoneException {
        return paymentService.initiatePayment(amount);
    }

    @GetMapping("/verify")
    public boolean verifyPayment(@RequestParam String orderId) throws PaymentNotDoneException {
        return paymentService.verifyPayment(orderId);
    }
    
    @PostMapping("/transaction")
    public void addTransaction(@RequestBody Order order, @PathVariable String paymentId) throws PaymentNotDoneException, RazorpayException {
        paymentService.addTransaction(order, paymentId);
    }
}
