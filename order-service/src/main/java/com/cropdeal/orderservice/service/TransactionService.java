package com.cropdeal.orderservice.service;

import com.cropdeal.orderservice.entity.Transaction;
import com.cropdeal.orderservice.exception.TransactionNotFoundException;
import com.cropdeal.orderservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(String id) throws TransactionNotFoundException {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(String id, Transaction updatedTransaction) throws TransactionNotFoundException {
        Transaction transaction = getTransactionById(id);
        // Update the fields of the existing transaction
        transaction.setUsername(updatedTransaction.getUsername());
        transaction.setTimestamp(updatedTransaction.getTimestamp());
        transaction.setPaymentId(updatedTransaction.getPaymentId());
        transaction.setAmount(updatedTransaction.getAmount());
        transaction.setType(updatedTransaction.getType());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(String id) throws TransactionNotFoundException {
        Transaction transaction = getTransactionById(id);
 
        transactionRepository.delete(transaction);
    }

	public Transaction getTransactionByPaymentId(String paymentId) throws TransactionNotFoundException {
		return transactionRepository.findByPaymentId(paymentId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found for orderId: " + paymentId));
	}
	
	public List<Transaction> getTransactionByUsername(String username) {
		return transactionRepository.findByUsername(username);
	}
}
