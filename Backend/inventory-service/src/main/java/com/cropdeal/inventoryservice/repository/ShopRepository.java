package com.cropdeal.inventoryservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.inventoryservice.entity.Shop;

@Repository
public interface ShopRepository extends MongoRepository <Shop, String> {

	List<Shop> findByFarmerUsername(String username);
}
