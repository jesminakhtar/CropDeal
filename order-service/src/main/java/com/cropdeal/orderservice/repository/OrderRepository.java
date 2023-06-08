package com.cropdeal.orderservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cropdeal.orderservice.model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {

}
