package com.cropdeal.orderservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.orderservice.exception.InvalidReceiptException;
import com.cropdeal.orderservice.model.Receipt;
import com.cropdeal.orderservice.service.ReceiptService;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PostMapping
    public ResponseEntity<Receipt> createReceipt(@RequestBody Receipt receipt) {
        Receipt createdReceipt = receiptService.createReceipt(receipt);
        return new ResponseEntity<>(createdReceipt, HttpStatus.CREATED);
    }

    @GetMapping("/{orderID}")
    public ResponseEntity<Receipt> getReceiptById(@PathVariable String orderID) {
        try {
            Receipt receipt = receiptService.getReceiptByOrderId(orderID);
            return new ResponseEntity<>(receipt, HttpStatus.OK);
        } catch (InvalidReceiptException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        List<Receipt> receipts = receiptService.getAllReceipts();
        return new ResponseEntity<>(receipts, HttpStatus.OK);
    }

    @DeleteMapping("/{orderID}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable String orderID) {
        try {
            receiptService.deleteReceipt(orderID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (InvalidReceiptException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

