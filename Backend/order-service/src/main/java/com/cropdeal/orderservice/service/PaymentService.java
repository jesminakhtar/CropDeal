package com.cropdeal.orderservice.service;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cropdeal.orderservice.dto.PaymentProcessingResponse;
import com.cropdeal.orderservice.entity.Transaction;
import com.cropdeal.orderservice.entity.TransactionType;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

	private final RazorpayClient razorpayClient;

	@Autowired
	private TransactionService transactionService;

	@Value("${razorpay.api.key}")
	private String apiKey;

	@Autowired
	public PaymentService(RazorpayClient razorpayClient) {
		this.razorpayClient = razorpayClient;
	}

	public PaymentProcessingResponse initiatePayment(double amount) throws PaymentNotDoneException {
		try {
			JSONObject orderRequest = new JSONObject();

			orderRequest.put("amount", amount); // amount in the smallest currency unit
			orderRequest.put("currency", "INR");

			log.info("OrderRequest : {}", orderRequest);

			Order razorpayOrder = razorpayClient.orders.create(orderRequest);
			String razorpayOrderId = razorpayOrder.get("id").toString();
			log.info("Payment initiated successfully. Razorpay Order ID: {}", razorpayOrderId);

			return new PaymentProcessingResponse(apiKey, "INR", razorpayOrderId, amount);

		} catch (RazorpayException e) {
			log.error("Payment initiating failed", e);
			throw new PaymentNotDoneException("Payment initiating failed");
		}
	}

	public boolean verifyPayment(String orderId) {
		log.info("verifying payment for {}", orderId);
		return true;
	}

	public void addTransaction(com.cropdeal.orderservice.entity.Order order, String paymentId)
			throws RazorpayException {
		Transaction transaction = new Transaction();
		transaction.setPaymentId(paymentId);
		transaction.setAmount(order.getTotalPrice());
		transaction.setUsername(order.getDealerId());
		transaction.setTimestamp(LocalDateTime.now());

		Payment payment = razorpayClient.payments.fetch(paymentId);

		// Determine the transaction type based on payment status
		TransactionType transactionType = null;
		
		if (payment.get("status").equals("credited")) {
			transactionType = TransactionType.CREDIT;
		} else {
			transactionType = TransactionType.DEBIT;
		}

		transaction.setType(transactionType);

		transactionService.createTransaction(transaction);
	}
}
