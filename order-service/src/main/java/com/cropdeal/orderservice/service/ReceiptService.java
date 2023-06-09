package com.cropdeal.orderservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropdeal.orderservice.exception.InvalidReceiptException;
import com.cropdeal.orderservice.model.Receipt;
import com.cropdeal.orderservice.repository.ReceiptRepository;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository repository;

    public Receipt createReceipt(Receipt receipt) {
        // Save the receipt to the database
        return repository.save(receipt);
    }

    public Receipt getReceiptByOrderId(String orderId) throws InvalidReceiptException {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new InvalidReceiptException("Receipt not found for order ID: " + orderId));
    }

    public List<Receipt> getAllReceipts() {
        return repository.findAll();
    }

    public void deleteReceipt(String orderId) throws InvalidReceiptException {
        Receipt receipt = getReceiptByOrderId(orderId);
        repository.delete(receipt);
    }
}
