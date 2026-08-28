package com.cropdeal.orderservice.entity;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "receipts")
public class Receipt {
	@Id
	private String id;
	private String orderId;
	private String transactionId;
	private String dealerId;
	private Map<String, Integer> orderItems; 
	private double totalPrice;
	private String status;
}