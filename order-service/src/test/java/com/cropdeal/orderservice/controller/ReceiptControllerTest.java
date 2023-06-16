package com.cropdeal.orderservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cropdeal.orderservice.entity.Receipt;
import com.cropdeal.orderservice.exception.ReceiptNotFoundException;
import com.cropdeal.orderservice.service.ReceiptService;

class ReceiptControllerTest {
    @Mock
    private ReceiptService receiptService;

    @InjectMocks
    private ReceiptController receiptController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReceipt() {
        // Prepare data
        Receipt inputReceipt = new Receipt();
        Receipt expectedReceipt = new Receipt();

        // Mock the receiptService.createReceipt() method
        when(receiptService.createReceipt(inputReceipt)).thenReturn(expectedReceipt);

        // Call the controller method
        ResponseEntity<Receipt> responseEntity = receiptController.createReceipt(inputReceipt);

        // Verify the result
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(expectedReceipt, responseEntity.getBody());
        verify(receiptService, times(1)).createReceipt(inputReceipt);
    }

    @Test
    void testGetReceiptById() throws ReceiptNotFoundException {
        // Prepare data
        String orderId = "123";
        Receipt expectedReceipt = new Receipt();

        // Mock the receiptService.getReceiptByOrderId() method
        when(receiptService.getReceiptByOrderId(orderId)).thenReturn(expectedReceipt);

        // Call the controller method
        ResponseEntity<Receipt> responseEntity = receiptController.getReceiptById(orderId);

        // Verify the result
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedReceipt, responseEntity.getBody());
        verify(receiptService, times(1)).getReceiptByOrderId(orderId);
    }

    @Test
    void testGetReceiptById_NotFound() throws ReceiptNotFoundException {
        // Prepare data
        String orderId = "123";

        // Mock the receiptService.getReceiptByOrderId() method to throw ReceiptNotFoundException
        when(receiptService.getReceiptByOrderId(orderId)).thenThrow(new ReceiptNotFoundException("Receipt not found."));

        // Call the controller method
        ResponseEntity<Receipt> responseEntity = receiptController.getReceiptById(orderId);

        // Verify the result
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(receiptService, times(1)).getReceiptByOrderId(orderId);
    }

    @Test
    void testGetAllReceipts() {
        // Prepare data
        List<Receipt> expectedReceipts = List.of(new Receipt(), new Receipt());

        // Mock the receiptService.getAllReceipts() method
        when(receiptService.getAllReceipts()).thenReturn(expectedReceipts);

        // Call the controller method
        ResponseEntity<List<Receipt>> responseEntity = receiptController.getAllReceipts();

        // Verify the result
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedReceipts, responseEntity.getBody());
        verify(receiptService, times(1)).getAllReceipts();
    }

    @Test
    void testDeleteReceipt() throws ReceiptNotFoundException {
        // Prepare data
        String orderId = "123";

        // Call the controller method
        ResponseEntity<Void> responseEntity = receiptController.deleteReceipt(orderId);

        // Verify the result
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(receiptService, times(1)).deleteReceipt(orderId);
    }

    @Test
    void testDeleteReceipt_NotFound() throws ReceiptNotFoundException {
        // Prepare data
        String orderId = "123";

        // Mock the receiptService.deleteReceipt() method to throw ReceiptNotFoundException
        doThrow(new ReceiptNotFoundException("Receipt not found.")).when(receiptService).deleteReceipt(orderId);

        // Call the controller method
        ResponseEntity<Void> responseEntity = receiptController.deleteReceipt(orderId);

        // Verify the result
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(receiptService, times(1)).deleteReceipt(orderId);
    }
}
