package com.cropdeal.usermanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import com.cropdeal.usermanagement.entity.BankAccount;
import com.cropdeal.usermanagement.exception.BankAccountNotFoundException;
import com.cropdeal.usermanagement.repository.BankAccountRepository;

class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private Logger log;

    @InjectMocks
    private BankAccountService bankAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBankAccounts_ShouldReturnListOfBankAccounts() {
        // Arrange
        List<BankAccount> bankAccounts = new ArrayList<>();
        BankAccount bankAccount1 = new BankAccount("1", "1234567890", "John Doe", "Bank of Example", "ABC123");
        BankAccount bankAccount2 = new BankAccount("2", "0987654321", "Jane Smith", "Example Bank", "XYZ789");
        bankAccounts.add(bankAccount1);
        bankAccounts.add(bankAccount2);

        when(bankAccountRepository.findAll()).thenReturn(bankAccounts);

        // Act
        List<BankAccount> result = bankAccountService.getAllBankAccounts();

        // Assert
        assertEquals(bankAccounts.size(), result.size());
        assertEquals(bankAccount1, result.get(0));
        assertEquals(bankAccount2, result.get(1));
    }

    @Test
    void getBankAccountById_WithExistingId_ShouldReturnBankAccount() throws BankAccountNotFoundException {
        // Arrange
        String bankAccountId = "1";
        BankAccount bankAccount = new BankAccount(bankAccountId, "1234567890", "John Doe", "Bank of Example", "ABC123");

        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(bankAccount));

        // Act
        BankAccount result = bankAccountService.getBankAccountById(bankAccountId);

        // Assert
        assertEquals(bankAccount, result);
    }

    @Test
    void getBankAccountById_WithNonExistingId_ShouldThrowBankAccountNotFoundException() {
        // Arrange
        String bankAccountId = "1";

        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getBankAccountById(bankAccountId));
    }

    @Test
    void createBankAccount_ShouldReturnCreatedBankAccount() {
        // Arrange
        BankAccount bankAccount = new BankAccount("1", "1234567890", "John Doe", "Bank of Example", "ABC123");

        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        // Act
        BankAccount result = bankAccountService.createBankAccount(bankAccount);

        // Assert
        assertEquals(bankAccount, result);
    }

    @Test
    void deleteBankAccount_WithExistingId_ShouldDeleteBankAccount() throws BankAccountNotFoundException {
        // Arrange
        String bankAccountId = "1";
        when(bankAccountRepository.existsById(bankAccountId)).thenReturn(true);

        // Act
        bankAccountService.deleteBankAccount(bankAccountId);

        // Assert
        verify(bankAccountRepository).deleteById(bankAccountId);
    }

    @Test
    void deleteBankAccount_WithNonExistingId_ShouldThrowBankAccountNotFoundException() {
        // Arrange
        String bankAccountId = "1";

        when(bankAccountRepository.existsById(bankAccountId)).thenReturn(false);

        // Act and Assert
        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.deleteBankAccount(bankAccountId));
        verify(bankAccountRepository, never()).deleteById(anyString());
    }
}
