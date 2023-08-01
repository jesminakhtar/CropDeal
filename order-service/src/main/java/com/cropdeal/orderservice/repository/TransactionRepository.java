package com.cropdeal.orderservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.orderservice.entity.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

	Optional<Transaction> findByPaymentId(String orderId);

	List<Transaction> findByUsername(String username);
}

