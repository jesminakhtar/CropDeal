package com.cropdeal.orderservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.service.ReceiptService;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptController.class);

    @Autowired
    private ReceiptService receiptService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Receipt> createReceipt(@RequestBody Receipt receipt) {
        logger.info("Creating receipt: {}", receipt);
        Receipt createdReceipt = receiptService.createReceipt(receipt);
        return new ResponseEntity<>(createdReceipt, HttpStatus.CREATED);
    }

    @GetMapping("/{orderID}")
    public ResponseEntity<Receipt> getReceiptById(@PathVariable String orderID) {
        try {
            logger.info("Fetching receipt by order ID: {}", orderID);
            Receipt receipt = receiptService.getReceiptByOrderId(orderID);
            return new ResponseEntity<>(receipt, HttpStatus.OK);
        } catch (ReceiptNotFoundException e) {
            logger.error("Receipt not found for order ID: {}", orderID);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        logger.info("Fetching all receipts");
        List<Receipt> receipts = receiptService.getAllReceipts();
        return new ResponseEntity<>(receipts, HttpStatus.OK);
    }

    @DeleteMapping("/{orderID}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteReceipt(@PathVariable String orderID) {
        try {
            logger.info("Deleting receipt with order ID: {}", orderID);
            receiptService.deleteReceipt(orderID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ReceiptNotFoundException e) {
            logger.error("Receipt not found for order ID: {}", orderID);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
