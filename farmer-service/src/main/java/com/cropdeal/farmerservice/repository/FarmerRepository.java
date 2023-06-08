package com.cropdeal.farmerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.cropdeal.farmerservice.model.Farmer;

public interface FarmerRepository extends MongoRepository<Farmer, String>{
	
}
