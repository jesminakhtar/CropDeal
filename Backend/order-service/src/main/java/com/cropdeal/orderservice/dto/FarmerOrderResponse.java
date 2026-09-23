package com.cropdeal.orderservice.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerOrderResponse {

    private String orderId;
    private String dealerId;
    private String status;
    private String paymentStatus;
    private String paymentMode;
    private String transactionId;
    private String deliveryAddressId;
    private Map<String, Integer> orderItems;
}