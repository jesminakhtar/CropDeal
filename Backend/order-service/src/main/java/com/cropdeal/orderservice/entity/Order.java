package com.cropdeal.orderservice.entity;

import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String orderId;

    private String dealerId;
    private double totalPrice;
    private Map<String, Integer> orderItems;
    private String status;
    private String deliveryAddressId;

    private String paymentStatus;
    private String paymentMode;
    private String transactionId;
    private String razorpayOrderId;
}