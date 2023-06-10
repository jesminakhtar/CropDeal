package com.cropdeal.inventoryservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.inventoryservice.entity.Product;

@Repository
public interface InventoryRepository extends MongoRepository<Product, String> {
}
