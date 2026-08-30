package com.cropdeal.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.orderservice.dto.PaymentProcessingResponse;
import com.cropdeal.orderservice.dto.PaymentVerificationRequest;
import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.CartNotFoundException;
import com.cropdeal.orderservice.exception.InvalidOrderException;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.service.OrderService;
import com.cropdeal.orderservice.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @PostMapping("/initiate/{orderId}")
    public ResponseEntity<PaymentProcessingResponse> initiatePayment(@PathVariable String orderId) throws PaymentNotDoneException {
        log.info("Initiating Razorpay payment for CropDeal order {}", orderId);

        PaymentProcessingResponse response = paymentService.initiatePayment(orderId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Receipt> verifyPayment(@RequestBody PaymentVerificationRequest request) throws PaymentNotDoneException, InvalidOrderException, CartNotFoundException {
        log.info("Verifying Razorpay payment for CropDeal order {}", request.getCropDealOrderId());

        String paymentMethod = paymentService.verifyPaymentAndGetMethod(request);

        Receipt receipt = orderService.completePaidOrder(
                request.getCropDealOrderId(),
                request.getRazorpayPaymentId(),
                paymentMethod
        );

        return ResponseEntity.ok(receipt);
    }
}