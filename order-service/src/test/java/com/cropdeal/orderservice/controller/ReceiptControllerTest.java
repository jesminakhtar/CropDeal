package com.cropdeal.orderservice.controller;

import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReceiptControllerTest {

    @Mock
    private ReceiptService receiptService;

    @InjectMocks
    private ReceiptController receiptController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReceipt_WithValidReceipt_ShouldReturnCreatedReceipt() {
        // Arrange
        Receipt receipt = new Receipt();
        when(receiptService.createReceipt(any(Receipt.class))).thenReturn(receipt);

        // Act
        ResponseEntity<Receipt> response = receiptController.createReceipt(receipt);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(receipt, response.getBody());
        verify(receiptService, times(1)).createReceipt(eq(receipt));
    }

    @Test
    void getReceiptById_WithExistingReceiptId_ShouldReturnReceipt() throws ReceiptNotFoundException {
        // Arrange
        String orderId = "123";
        Receipt receipt = new Receipt();
        when(receiptService.getReceiptByOrderId(orderId)).thenReturn(receipt);

        // Act
        ResponseEntity<Receipt> response = receiptController.getReceiptById(orderId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(receipt, response.getBody());
        verify(receiptService, times(1)).getReceiptByOrderId(orderId);
    }

    @Test
    void getReceiptById_WithNonExistingReceiptId_ShouldReturnNotFound() throws ReceiptNotFoundException {
        // Arrange
        String orderId = "123";
        doThrow(new ReceiptNotFoundException("")).when(receiptService).getReceiptByOrderId(orderId);

        // Act
        ResponseEntity<Receipt> response = receiptController.getReceiptById(orderId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(receiptService, times(1)).getReceiptByOrderId(orderId);
    }

    @Test
    void getAllReceipts_ShouldReturnAllReceipts() {
        // Arrange
        List<Receipt> receipts = new ArrayList<>();
        receipts.add(new Receipt());
        receipts.add(new Receipt());
        when(receiptService.getAllReceipts()).thenReturn(receipts);

        // Act
        ResponseEntity<List<Receipt>> response = receiptController.getAllReceipts();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(receipts, response.getBody());
        verify(receiptService, times(1)).getAllReceipts();
    }

    @Test
    void deleteReceipt_WithExistingReceiptId_ShouldReturnNoContent() throws ReceiptNotFoundException {
        // Arrange
        String orderId = "123";
        doNothing().when(receiptService).deleteReceipt(orderId);

        // Act
        ResponseEntity<Void> response = receiptController.deleteReceipt(orderId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(receiptService, times(1)).deleteReceipt(orderId);
    }

    @Test
    void deleteReceipt_WithNonExistingReceiptId_ShouldReturnNotFound() throws ReceiptNotFoundException {
        // Arrange
        String orderId = "123";
        doThrow(new ReceiptNotFoundException("")).when(receiptService).deleteReceipt(orderId);

        // Act
        ResponseEntity<Void> response = receiptController.deleteReceipt(orderId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(receiptService, times(1)).deleteReceipt(orderId);
    }
}
