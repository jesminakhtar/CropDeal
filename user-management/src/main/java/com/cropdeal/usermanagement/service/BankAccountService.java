package com.cropdeal.usermanagement.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropdeal.usermanagement.entity.BankAccount;
import com.cropdeal.usermanagement.exception.BankAccountNotFoundException;
import com.cropdeal.usermanagement.repository.BankAccountRepository;

@Service
public class BankAccountService {
    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);

    @Autowired
    private BankAccountRepository bankAccountRepository;

    public List<BankAccount> getAllBankAccounts() {
        logger.info("Fetching all bank accounts");
        return bankAccountRepository.findAll();
    }

    public BankAccount getBankAccountById(String id) throws BankAccountNotFoundException {
        logger.info("Fetching bank account by ID: {}", id);
        BankAccount bankAccount = bankAccountRepository.findById(id).orElseThrow(() -> {
            logger.error("Bank account not found with ID: {}", id);
            return new BankAccountNotFoundException("Bank account not found with ID: " + id);
        });
        return bankAccount;
    }

    public BankAccount createBankAccount(BankAccount bankAccount) {
        logger.info("Creating bank account: {}", bankAccount);
        return bankAccountRepository.save(bankAccount);
    }

    public void deleteBankAccount(String id) throws BankAccountNotFoundException {
        logger.info("Deleting bank account with ID: {}", id);
        if (!bankAccountRepository.existsById(id)) {
            logger.error("Bank account not found with ID: {}", id);
            throw new BankAccountNotFoundException("Bank account not found with ID: " + id);
        }
        bankAccountRepository.deleteById(id);
    }
}
