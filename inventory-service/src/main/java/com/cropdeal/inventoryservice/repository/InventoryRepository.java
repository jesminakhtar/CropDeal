package com.cropdeal.inventoryservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.inventoryservice.entity.Product;

@Repository
public interface InventoryRepository extends MongoRepository<Product, String> {
	Optional<Product> findByFarmerIdAndName(String farmerId, String name);
    boolean existsByFarmerIdAndName(String farmerId, String name);
    List<Product> findByFarmerId(String farmerId);
}
