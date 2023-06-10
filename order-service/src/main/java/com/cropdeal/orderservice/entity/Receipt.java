package com.cropdeal.orderservice.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cropdeal.orderservice.model.Crop;

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
    private List<Crop> orderItems;
    private double totalPrice;
    private String status;
    
    public Receipt() {
	}
    
	public Receipt(String orderId, String razorpayOrderId, String dealerId, List<Crop> orderItems, double totalPrice,
			String status) {
		super();
		this.orderId = orderId;
		this.razorpayOrderId = razorpayOrderId;
		this.dealerId = dealerId;
		this.orderItems = orderItems;
		this.totalPrice = totalPrice;
		this.status = status;
	}
    
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getDealerId() {
		return dealerId;
	}

	public void setDealerId(String dealerId) {
		this.dealerId = dealerId;
	}

	public List<Crop> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<Crop> orderItems) {
		this.orderItems = orderItems;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
