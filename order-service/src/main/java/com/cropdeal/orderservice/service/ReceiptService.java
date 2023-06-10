package com.cropdeal.orderservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.repository.ReceiptRepository;

@Service
public class ReceiptService {

	@Autowired
	private ReceiptRepository repository;

	public Receipt createReceipt(Receipt receipt) {
		// Save the receipt to the database
		return repository.save(receipt);
	}

	public Receipt getReceiptByOrderId(String orderId) throws ReceiptNotFoundException {
		Optional<Receipt> optionalReceipt = repository.findByOrderId(orderId);
		if (optionalReceipt.isPresent()) {
			return optionalReceipt.get();
		} else {
			throw new ReceiptNotFoundException("Receipt not found for order ID: " + orderId);
		}
	}

	public List<Receipt> getAllReceipts() {
		return repository.findAll();
	}

	public void updateReceipt(String orderId, Receipt updatedReceipt) throws ReceiptNotFoundException {
		Receipt receipt = getReceiptByOrderId(orderId);
		if (receipt != null) {
			receipt.setDealerId(updatedReceipt.getDealerId());
			receipt.setOrderItems(updatedReceipt.getOrderItems());
			receipt.setTotalPrice(updatedReceipt.getTotalPrice());
			receipt.setStatus(updatedReceipt.getStatus());
			repository.save(receipt);
		} else {
			throw new ReceiptNotFoundException("Receipt not found for order ID: " + orderId);
		}
	}

	public void deleteReceipt(String orderId) throws ReceiptNotFoundException {
		Receipt receipt = getReceiptByOrderId(orderId);
		if (receipt != null) {
			repository.delete(receipt);
		} else {
			throw new ReceiptNotFoundException("Receipt not found for order ID: " + orderId);
		}
	}
}
