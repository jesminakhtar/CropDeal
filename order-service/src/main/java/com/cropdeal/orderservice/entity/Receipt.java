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
	private String razorpayOrderId;
	private String dealerId;
	private Map<String, Integer> orderItems; // Updated to use a map of product IDs and quantities
	private double totalPrice;
	private String status;
	
//	public Receipt() {}
//
//	public Receipt(String orderId, String razorpayOrderId, String dealerId, Map<String, Integer> orderItems,
//			double totalPrice, String status) {
//		this.orderId = orderId;
//		this.razorpayOrderId = razorpayOrderId;
//		this.dealerId = dealerId;
//		this.orderItems = orderItems;
//		this.totalPrice = totalPrice;
//		this.status = status;
//	}
//
//	public String getOrderId() {
//		return orderId;
//	}
//
//	public void setOrderId(String orderId) {
//		this.orderId = orderId;
//	}
//
//	public String getRazorpayOrderId() {
//		return razorpayOrderId;
//	}
//
//	public void setRazorpayOrderId(String razorpayOrderId) {
//		this.razorpayOrderId = razorpayOrderId;
//	}
//
//	public String getDealerId() {
//		return dealerId;
//	}
//
//	public void setDealerId(String dealerId) {
//		this.dealerId = dealerId;
//	}
//
//	public Map<String, Integer> getOrderItems() {
//		return orderItems;
//	}
//
//	public void setOrderItems(Map<String, Integer> orderItems) {
//		this.orderItems = orderItems;
//	}
//
//	public double getTotalPrice() {
//		return totalPrice;
//	}
//
//	public void setTotalPrice(double totalPrice) {
//		this.totalPrice = totalPrice;
//	}
//
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}

}