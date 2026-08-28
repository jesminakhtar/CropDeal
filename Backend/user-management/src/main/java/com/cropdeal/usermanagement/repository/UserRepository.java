package com.cropdeal.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cropdeal.usermanagement.entity.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}


