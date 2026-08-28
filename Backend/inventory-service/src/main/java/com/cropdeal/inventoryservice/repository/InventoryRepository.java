package com.cropdeal.inventoryservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.inventoryservice.entity.Product;

@Repository
public interface InventoryRepository extends MongoRepository<Product, String> {
    boolean existsByShopIdAndName(String shopId, String name);
	Optional<Product> findByShopIdAndName(String shopId, String name);
}
