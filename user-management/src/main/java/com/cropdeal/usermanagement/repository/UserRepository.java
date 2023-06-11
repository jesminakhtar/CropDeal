package com.cropdeal.usermanagement.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cropdeal.usermanagement.entity.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
}

