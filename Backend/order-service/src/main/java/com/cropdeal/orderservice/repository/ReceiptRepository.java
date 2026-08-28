package com.cropdeal.orderservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cropdeal.orderservice.entity.Receipt;


public interface ReceiptRepository extends MongoRepository<Receipt, String>{

	Optional<Receipt> findByOrderId(String orderId);

	List<Receipt> findByDealerId(String userId);

}
