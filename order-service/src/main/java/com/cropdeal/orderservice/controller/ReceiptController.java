package com.cropdeal.orderservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.service.ReceiptService;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PreAuthorize("hasAnyAuthority('Admin', 'SCOPE_internal')")
    @PostMapping
    public ResponseEntity<Receipt> createReceipt(@RequestBody Receipt receipt) {
        Receipt createdReceipt = receiptService.createReceipt(receipt);
        return new ResponseEntity<>(createdReceipt, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('Admin', 'SCOPE_internal')")
    @GetMapping("/{orderID}")
    public ResponseEntity<Receipt> getReceiptById(@PathVariable String orderID) {
        try {
            Receipt receipt = receiptService.getReceiptByOrderId(orderID);
            return new ResponseEntity<>(receipt, HttpStatus.OK);
        } catch (ReceiptNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('Admin', 'SCOPE_internal')")
    @GetMapping("/all")
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        List<Receipt> receipts = receiptService.getAllReceipts();
        return new ResponseEntity<>(receipts, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('Admin', 'SCOPE_internal')")
    @DeleteMapping("/{orderID}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable String orderID) {
        try {
            receiptService.deleteReceipt(orderID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ReceiptNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
