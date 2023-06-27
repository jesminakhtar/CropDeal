//package com.cropdeal.orderservice.service;
//
//import com.cropdeal.orderservice.entity.Cart;
//import com.cropdeal.orderservice.entity.Order;
//import com.cropdeal.orderservice.entity.Receipt;
//import com.cropdeal.orderservice.exception.*;
//import com.cropdeal.orderservice.model.Product;
//import com.cropdeal.orderservice.repository.OrderRepository;
//
//import jakarta.inject.Inject;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//class OrderServiceTest {
//
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @Mock
//    private CartService cartService;
//
//    @Mock
//    private ReceiptService receiptService;
//    
//    @InjectMocks
//    private OrderService orderService;
//    
//
////    @Mock
////    private PaymentService paymentService;
//
//    private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void getAllOrders_OrdersRetrievedSuccessfully() {
//        // Arrange
//        Order order = new Order();
//        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
//
//        // Act
//        List<Order> result = orderService.getAllOrders();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(order, result.get(0));
//    }
//
//    @Test
//    void getOrderById_ValidOrderId_OrderRetrievedSuccessfully() throws InvalidOrderException {
//        // Arrange
//        String orderId = "123";
//        Order order = new Order();
//        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
//
//        // Act
//        Order result = orderService.getOrderById(orderId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(order, result);
//    }
//
//    @Test
//    void getOrderById_InvalidOrderId_InvalidOrderExceptionThrown() {
//        // Arrange
//        String orderId = "123";
//        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());
//
//        // Act & Assert
//        assertThrows(InvalidOrderException.class, () -> orderService.getOrderById(orderId));
//    }
//
//    @Test
//    void placeOrderFromCart_CartExistsAndPaymentDone_OrderPlacedSuccessfully() throws CartNotFoundException, PaymentNotDoneException {
//        // Arrange
//        String dealerId = "123";
//        Cart cart = new Cart(dealerId, new HashMap<>());
//        Order order = new Order(dealerId, new HashMap<>());
//        Receipt receipt = new Receipt();
//
//        when(cartService.getCartByDealerId(dealerId)).thenReturn(cart);
//        doNothing().when(cartService).clearCart();
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//        when(receiptService.createReceipt(any(Receipt.class))).thenReturn(receipt);
//
//        // Act
//        Receipt result = orderService.placeOrderFromCart();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(receipt, result);
//    }
//
//    @Test
//    void placeOrderFromCart_CartNotFound_CartNotFoundExceptionThrown() throws CartNotFoundException, PaymentNotDoneException {
//        // Arrange
//        String dealerId = "123";
//        when(cartService.getCartByDealerId(dealerId)).thenThrow(CartNotFoundException.class);
//
//        // Act & Assert
//        assertThrows(CartNotFoundException.class, () -> orderService.placeOrderFromCart());
//    }
//
//    @Test
//    void placeOrderDirectly_ValidProductAndPaymentDone_OrderPlacedSuccessfully() throws InvalidProductException, PaymentNotDoneException {
//        // Arrange
//        String productId = "123";
//        int quantity = 2;
//        Order order = new Order();
//        Receipt receipt = new Receipt();
//
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//        when(receiptService.createReceipt(any(Receipt.class))).thenReturn(receipt);
//
//        // Act
//        Receipt result = orderService.placeOrderDirectly(productId, quantity);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(receipt, result);
//    }
//
//    @Test
//    void placeOrderDirectly_InvalidProduct_InvalidProductExceptionThrown() throws InvalidProductException, PaymentNotDoneException {
//        // Arrange
//        String productId = "123";
//        int quantity = 2;
//
//        when(restTemplate.getForObject(anyString(), any())).thenReturn(null);
//
//        // Act & Assert
//        assertThrows(InvalidProductException.class, () -> orderService.placeOrderDirectly(productId, quantity));
//    }
//
//    @Test
//    void createOrder_PaymentDone_OrderCreatedSuccessfully() throws PaymentNotDoneException {
//        // Arrange
//        Order order = new Order();
//        Receipt receipt = new Receipt();
//
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//        when(receiptService.createReceipt(any(Receipt.class))).thenReturn(receipt);
//
//        // Act
//        Receipt result = orderService.createOrder(order);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(receipt, result);
//    }
//
//    @Test
//    void createOrder_PaymentNotDone_PaymentNotDoneExceptionThrown() {
//        // Arrange
//        Order order = new Order();
//
//        // Act & Assert
//        assertThrows(PaymentNotDoneException.class, () -> orderService.createOrder(order));
//    }
//
//    @Test
//    void cancelOrder_ValidOrderId_OrderCancelledSuccessfully() throws InvalidOrderException, ReceiptNotFoundException, PaymentNotDoneException {
//        // Arrange
//        String orderId = "123";
//        Order order = new Order();
//        Receipt receipt = new Receipt();
//
//        when(orderService.getOrderById(orderId)).thenReturn(order);
//        when(receiptService.getReceiptByOrderId(orderId)).thenReturn(receipt);
//        doNothing().when(receiptService).updateReceipt(orderId, receipt);
//
//        // Act
//        orderService.cancelOrder(orderId);
//
//        // Assert
//        verify(orderRepository, times(1)).delete(order);
//        assertEquals("Cancelled", receipt.getStatus());
//    }
//
//    @Test
//    void cancelOrder_InvalidOrderId_InvalidOrderExceptionThrown() throws InvalidOrderException, ReceiptNotFoundException, PaymentNotDoneException {
//        // Arrange
//        String orderId = "123";
//        when(orderService.getOrderById(orderId)).thenThrow(InvalidOrderException.class);
//
//        // Act & Assert
//        assertThrows(InvalidOrderException.class, () -> orderService.cancelOrder(orderId));
//    }
//
//    @Test
//    void updateOrder_ValidOrderId_OrderUpdatedSuccessfully() throws InvalidOrderException, ReceiptNotFoundException, PaymentNotDoneException {
//        // Arrange
//        String orderId = "123";
//        Order order = new Order();
//        Order updatedOrder = new Order();
//        Receipt receipt = new Receipt();
//
//        when(orderService.getOrderById(orderId)).thenReturn(order);
//        when(receiptService.getReceiptByOrderId(orderId)).thenReturn(receipt);
//        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
//
//        // Act
//        Order result = orderService.updateOrder(orderId, updatedOrder);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(updatedOrder, result);
//    }
//
//    @Test
//    void updateOrder_InvalidOrderId_InvalidOrderExceptionThrown() throws InvalidOrderException, ReceiptNotFoundException, PaymentNotDoneException {
//        // Arrange
//        String orderId = "123";
//        Order updatedOrder = new Order();
//
//        when(orderService.getOrderById(orderId)).thenThrow(InvalidOrderException.class);
//
//        // Act & Assert
//        assertThrows(InvalidOrderException.class, () -> orderService.updateOrder(orderId, updatedOrder));
//    }
//
//    @Test
//    void updateOrder_PaymentDone_PaymentAdjustmentProcessedSuccessfully() throws InvalidOrderException, ReceiptNotFoundException, PaymentNotDoneException {
//        // Arrange
//        String orderId = "123";
//        Order order = new Order();
//        Order updatedOrder = new Order();
//        Receipt receipt = new Receipt();
//
//        when(orderService.getOrderById(orderId)).thenReturn(order);
//        when(receiptService.getReceiptByOrderId(orderId)).thenReturn(receipt);
////        when(receipt.getRazorpayOrderId()).thenReturn("123");
////        when(receipt.getStatus()).thenReturn("Paid");
////        when(paymentService.processPaymentAdjustment(anyString(), anyDouble())).thenReturn(null);
//        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
//
//        // Act
//        Order result = orderService.updateOrder(orderId, updatedOrder);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(updatedOrder, result);
////        verify(paymentService, times(1)).processPaymentAdjustment(eq("123"), anyDouble());
//    }
//
//    // Test other methods
//
//a    @Test
//    void updateInventory_InventoryUpdatedSuccessfully() {
//        // Arrange
//        String productId = "123";
//        int quantity = 2;
//        String url = INVENTORY_SERVICE_URL + "/products/" + productId + "/updateQuantity?quantity=" + quantity;
//
//        // Act
//        orderService.updateInventory(productId, quantity);
//
//        // Assert
//        verify(restTemplate, times(1)).put(eq(url), isNull());
//    }
//
//    @Test
//    void calculateTotalPrice_TotalPriceCalculatedCorrectly() {
//        // Arrange
//        Map<String, Integer> orderItems = new HashMap<>();
//        orderItems.put("123", 2);
//        Product product = new Product();
//        product.setPrice(50.0);
//
//        when(orderService.getProductById("123")).thenReturn(product);
//
//        // Act
//        double result = orderService.calculateTotalPrice(orderItems);
//
//        // Assert
//        assertEquals(100.0, result);
//    }
//
//    @Test
//    void getProductById_ProductRetrievedSuccessfully() {
//        // Arrange
//        String productId = "123";
//        String url = INVENTORY_SERVICE_URL + "/products/findById/" + productId;
//        Product product = new Product();
//
//        when(restTemplate.getForObject(url, Product.class)).thenReturn(product);
//
//        // Act
//        Product result = orderService.getProductById(productId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(product, result);
//    }
//
//    @Test
//    void generateOrderId_OrderIdGeneratedSuccessfully() {
//        // Act
//        String orderId = orderService.generateOrderId();
//
//        // Assert
//        assertNotNull(orderId);
//        assertEquals(16, orderId.length());
//    }
//
//    @Test
//    void retrieveUserId_UserIdRetrievedSuccessfully() {
//        // Arrange
//        String userId = "user123";
//        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(userId);
//
//        // Act
//        String result = orderService.retrieveUserId();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(userId, result);
//    }
//}
