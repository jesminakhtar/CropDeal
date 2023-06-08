package com.cropdeal.inventoryservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.inventoryservice.model.Crop;

@Repository
public interface InventoryRepository extends MongoRepository<Crop, String> {
}
