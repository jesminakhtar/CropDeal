package com.cropdeal.usermanagement.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.usermanagement.entity.BankAccount;

@Repository
public interface BankAccountRepository extends MongoRepository<BankAccount, String> {
}
