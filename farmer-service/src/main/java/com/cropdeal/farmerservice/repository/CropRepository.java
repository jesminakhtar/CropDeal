package com.cropdeal.farmerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cropdeal.farmerservice.model.Crop;

public interface CropRepository extends MongoRepository<Crop, String> {

}
