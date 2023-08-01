package com.cropdeal.orderservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cropdeal.orderservice.entity.Order;

public interface OrderRepository extends MongoRepository<Order, String> {

	List<Order> findByDealerId(String dealerId);

}
