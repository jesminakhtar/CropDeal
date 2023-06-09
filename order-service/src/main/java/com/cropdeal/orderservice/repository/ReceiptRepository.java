package com.cropdeal.orderservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cropdeal.orderservice.model.Receipt;

public interface ReceiptRepository extends MongoRepository<Receipt, String>{

	Optional<Receipt> findByOrderId(String orderId);

}
