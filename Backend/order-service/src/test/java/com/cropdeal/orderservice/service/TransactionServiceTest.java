package com.cropdeal.orderservice.service;

import com.cropdeal.orderservice.entity.Transaction;
import com.cropdeal.orderservice.entity.TransactionType;
import com.cropdeal.orderservice.exception.TransactionNotFoundException;
import com.cropdeal.orderservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
	
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        // Arrange
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("1", "user1", LocalDateTime.now(), "payment1", 100.0, TransactionType.CREDIT));
        transactions.add(new Transaction("2", "user2", LocalDateTime.now(), "payment2", 200.0, TransactionType.DEBIT));
        when(transactionRepository.findAll()).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getAllTransactions();

        // Assert
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }

    @Test
    void getTransactionById_ExistingId_ShouldReturnTransaction() throws TransactionNotFoundException {
        // Arrange
        Transaction transaction = new Transaction("1", "user1", LocalDateTime.now(), "payment1", 100.0, TransactionType.CREDIT);
        when(transactionRepository.findById("1")).thenReturn(Optional.of(transaction));

        // Act
        Transaction result = transactionService.getTransactionById("1");

        // Assert
        assertNotNull(result);
        assertEquals("user1", result.getUsername());
    }

    @Test
    void getTransactionById_NonExistingId_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findById("1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransactionById("1"));
    }

    @Test
    void createTransaction_ShouldSaveAndReturnTransaction() {
        // Arrange
        Transaction transaction = new Transaction("1", "user1", LocalDateTime.now(), "payment1", 100.0, TransactionType.CREDIT);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // Act
        Transaction result = transactionService.createTransaction(transaction);

        // Assert
        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void updateTransaction_ExistingId_ShouldUpdateAndReturnTransaction() throws TransactionNotFoundException {
        // Arrange
        Transaction existingTransaction = new Transaction("1", "user1", LocalDateTime.now(), "payment1", 100.0, TransactionType.CREDIT);
        Transaction updatedTransaction = new Transaction("1", "user2", LocalDateTime.now(), "payment2", 200.0, TransactionType.DEBIT);
        when(transactionRepository.findById("1")).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(existingTransaction)).thenReturn(existingTransaction);

        // Act
        Transaction result = transactionService.updateTransaction("1", updatedTransaction);

        // Assert
        assertNotNull(result);
        assertEquals("user2", result.getUsername());
        verify(transactionRepository, times(1)).save(existingTransaction);
    }

    @Test
    void updateTransaction_NonExistingId_ShouldThrowException() {
        // Arrange
        Transaction updatedTransaction = new Transaction("1", "user2", LocalDateTime.now(), "payment2", 200.0, TransactionType.DEBIT);
        when(transactionRepository.findById("1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> transactionService.updateTransaction("1", updatedTransaction));
    }

    @Test
    void deleteTransaction_ExistingId_ShouldDeleteTransaction() throws TransactionNotFoundException {
        // Arrange
        Transaction transaction = new Transaction("1", "user1", LocalDateTime.now(), "payment1", 100.0, TransactionType.CREDIT);
        when(transactionRepository.findById("1")).thenReturn(Optional.of(transaction));

        // Act
        transactionService.deleteTransaction("1");

        // Assert
        verify(transactionRepository, times(1)).delete(transaction);
    }

    @Test
    void deleteTransaction_NonExistingId_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findById("1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> transactionService.deleteTransaction("1"));
    }

    @Test
    void getTransactionByPaymentId_ExistingPaymentId_ShouldReturnTransaction() throws TransactionNotFoundException {
        // Arrange
        Transaction transaction = new Transaction("1", "user1", LocalDateTime.now(), "payment1", 100.0, TransactionType.CREDIT);
        when(transactionRepository.findByPaymentId("payment1")).thenReturn(Optional.of(transaction));

        // Act
        Transaction result = transactionService.getTransactionByPaymentId("payment1");

        // Assert
        assertNotNull(result);
        assertEquals("user1", result.getUsername());
    }

    @Test
    void getTransactionByPaymentId_NonExistingPaymentId_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findByPaymentId("payment1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransactionByPaymentId("payment1"));
    }

    @Test
    void getTransactionByUsername_ExistingUsername_ShouldReturnTransactions() throws TransactionNotFoundException {
        // Arrange
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("1", "user1", LocalDateTime.now(), "payment1", 100.0, TransactionType.CREDIT));
        transactions.add(new Transaction("2", "user1", LocalDateTime.now(), "payment2", 200.0, TransactionType.DEBIT));
        when(transactionRepository.findByUsername("user1")).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionByUsername("user1");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user1", result.get(1).getUsername());
    }

 // NonExistingUsername Test
    @Test
    void getTransactionByUsername_NonExistingUsername_ShouldReturnEmptyList() throws TransactionNotFoundException {
        // Arrange
        String username = "user1";
        when(transactionRepository.findByUsername(username)).thenReturn(new ArrayList<>());

        // Act
        List<Transaction> result = transactionService.getTransactionByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
