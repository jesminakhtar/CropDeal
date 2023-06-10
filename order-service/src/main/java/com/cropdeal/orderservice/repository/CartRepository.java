package com.cropdeal.orderservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.orderservice.entity.Cart;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    Optional<Cart> findByDealerId(String dealerId);
}

