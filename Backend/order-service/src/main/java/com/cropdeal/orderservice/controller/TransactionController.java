package com.cropdeal.orderservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.orderservice.entity.Transaction;
import com.cropdeal.orderservice.exception.TransactionNotFoundException;
import com.cropdeal.orderservice.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String id) throws TransactionNotFoundException {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Transaction>> getTransactionByUsername(@PathVariable String username) throws TransactionNotFoundException {
    	List<Transaction> transactions = transactionService.getTransactionByUsername(username);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable("id") String id,
            @RequestBody Transaction updatedTransaction) throws TransactionNotFoundException {
        Transaction updatedTransactionObj = transactionService.updateTransaction(id, updatedTransaction);
        return ResponseEntity.ok(updatedTransactionObj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable("id") String id) throws TransactionNotFoundException {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
