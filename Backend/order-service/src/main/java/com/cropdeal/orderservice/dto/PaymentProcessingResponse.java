package com.cropdeal.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessingResponse {
    private String apikey;
    private String currency;
    private String orderId;
    private long amount;
}