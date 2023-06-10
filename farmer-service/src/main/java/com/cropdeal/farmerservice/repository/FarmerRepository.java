package com.cropdeal.farmerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cropdeal.farmerservice.enity.Farmer;

public interface FarmerRepository extends MongoRepository<Farmer, String>{
	
}
