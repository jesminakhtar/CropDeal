package com.cropdeal.orderservice.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String username;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String paymentId;
    private double amount;
    private TransactionType type;
    
}
