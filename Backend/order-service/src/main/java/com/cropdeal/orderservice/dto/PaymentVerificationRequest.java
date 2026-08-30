package com.cropdeal.orderservice.dto;

import lombok.Data;

@Data
public class PaymentVerificationRequest {

    private String cropDealOrderId;
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
}