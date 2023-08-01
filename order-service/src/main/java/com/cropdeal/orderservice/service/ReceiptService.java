package com.cropdeal.orderservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.repository.ReceiptRepository;

@Service
public class ReceiptService {

	Logger log = LoggerFactory.getLogger(ReceiptService.class);

	@Autowired
	private ReceiptRepository repository;

	public Receipt createReceipt(Receipt receipt) {
		// Save the receipt to the database
		receipt.setId(generateUniqueReceiptId());
		Receipt savedReceipt = repository.save(receipt);
		log.info("Receipt created with ID: {}", savedReceipt.getId());
		return savedReceipt;
	}

	public Receipt getReceiptByOrderId(String orderId) throws ReceiptNotFoundException {
		Optional<Receipt> optionalReceipt = repository.findByOrderId(orderId);
		if (optionalReceipt.isPresent()) {
			return optionalReceipt.get();
		} else {
			throw new ReceiptNotFoundException("Receipt not found for order ID: " + orderId);
		}
	}
	
	public List<Receipt> getReceiptByUserId(String userId) throws ReceiptNotFoundException {
		
		return repository.findByDealerId(userId);
//		if (optionalReceipt.isPresent()) {
//			return optionalReceipt.get();
//		} else {
//			throw new ReceiptNotFoundException("Receipt not found for user: " + userId);
//		}
	}

	public List<Receipt> getAllReceipts() {
		return repository.findAll();
	}

	public void updateReceipt(String orderId, Receipt updatedReceipt) throws ReceiptNotFoundException {
		Receipt receipt = getReceiptByOrderId(orderId);
		receipt.setDealerId(updatedReceipt.getDealerId());
		receipt.setOrderItems(updatedReceipt.getOrderItems());
		receipt.setTotalPrice(updatedReceipt.getTotalPrice());
		receipt.setStatus(updatedReceipt.getStatus());
		receipt = repository.save(receipt);
		log.info("Receipt updated with ID: {}", receipt.getId());

	}

	public void deleteReceipt(String orderId) throws ReceiptNotFoundException {
		Receipt receipt = getReceiptByOrderId(orderId);
		repository.delete(receipt);
		log.info("Receipt deleted with ID: {}", receipt.getId());
	}

	public String generateUniqueReceiptId() {
		String uniqueId = UUID.randomUUID().toString();
		return uniqueId.replace("-", "").substring(0, 6);
	}

	
}
