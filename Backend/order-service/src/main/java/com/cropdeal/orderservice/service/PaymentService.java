package com.cropdeal.orderservice.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cropdeal.orderservice.dto.PaymentProcessingResponse;
import com.cropdeal.orderservice.dto.PaymentVerificationRequest;
import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.cropdeal.orderservice.repository.OrderRepository;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

@Service
public class PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

	private final RazorpayClient razorpayClient;
	private final OrderRepository orderRepository;

	@Value("${razorpay.api.key}")
	private String apiKey;

	@Value("${razorpay.api.secret}")
	private String apiSecret;

	public PaymentService(RazorpayClient razorpayClient, OrderRepository orderRepository) {
		this.razorpayClient = razorpayClient;
		this.orderRepository = orderRepository;
	}

	public PaymentProcessingResponse initiatePayment(String cropDealOrderId) throws PaymentNotDoneException {

		if (apiKey == null || apiKey.isBlank() || apiSecret == null || apiSecret.isBlank()) {
			throw new PaymentNotDoneException("Razorpay credentials are not configured");
		}

		Order order = orderRepository.findById(cropDealOrderId)
				.orElseThrow(() -> new PaymentNotDoneException("CropDeal order not found: " + cropDealOrderId));

		if (!"Pending".equalsIgnoreCase(order.getStatus())) {
			throw new PaymentNotDoneException("Payment can only be initiated for pending orders");
		}

		if ("Done".equalsIgnoreCase(order.getPaymentStatus())) {
			throw new PaymentNotDoneException("This order has already been paid");
		}

		long amountInPaise = Math.round(order.getTotalPrice() * 100);

		if (amountInPaise <= 0) {
			throw new PaymentNotDoneException("Invalid order amount");
		}

		if (order.getRazorpayOrderId() != null && !order.getRazorpayOrderId().isBlank()) {
			log.info("Reusing Razorpay order {} for CropDeal order {}", order.getRazorpayOrderId(), cropDealOrderId);

			return new PaymentProcessingResponse(
					apiKey,
					"INR",
					order.getRazorpayOrderId(),
					amountInPaise
			);
		}

		try {
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amountInPaise);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", cropDealOrderId);

			com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);

			String razorpayOrderId = razorpayOrder.get("id");

			order.setRazorpayOrderId(razorpayOrderId);
			orderRepository.save(order);

			log.info("Razorpay order {} created for CropDeal order {}", razorpayOrderId, cropDealOrderId);

			return new PaymentProcessingResponse(
					apiKey,
					"INR",
					razorpayOrderId,
					amountInPaise
			);

		} catch (RazorpayException e) {
			log.error("Unable to create Razorpay order for {}", cropDealOrderId, e);
			throw new PaymentNotDoneException("Unable to initiate Razorpay payment");
		}
	}

	public String verifyPaymentAndGetMethod(PaymentVerificationRequest request) throws PaymentNotDoneException {

		validateVerificationRequest(request);

		Order order = orderRepository.findById(request.getCropDealOrderId())
				.orElseThrow(() -> new PaymentNotDoneException(
						"CropDeal order not found: " + request.getCropDealOrderId()
				));

		if (order.getRazorpayOrderId() == null || !order.getRazorpayOrderId().equals(request.getRazorpayOrderId())) {
			throw new PaymentNotDoneException("Razorpay order ID does not match this CropDeal order");
		}

		try {
			verifySignature(order, request);

			Payment payment = razorpayClient.payments.fetch(request.getRazorpayPaymentId());

			validatePaymentOrder(payment, order);
			validatePaymentAmount(payment, order);
			validatePaymentCurrency(payment);

			payment = ensurePaymentCaptured(payment, request.getRazorpayPaymentId(), order);

			String paymentMethod = payment.get("method");

			if (paymentMethod == null || paymentMethod.isBlank()) {
				paymentMethod = "RAZORPAY";
			}

			paymentMethod = paymentMethod.toUpperCase();

			log.info(
					"Razorpay payment {} verified successfully for CropDeal order {} using {}",
					request.getRazorpayPaymentId(),
					request.getCropDealOrderId(),
					paymentMethod
			);

			return paymentMethod;

		} catch (RazorpayException e) {
			log.error(
					"Razorpay verification failed for CropDeal order {}",
					request.getCropDealOrderId(),
					e
			);

			throw new PaymentNotDoneException("Unable to verify Razorpay payment");
		}
	}

	private void validateVerificationRequest(PaymentVerificationRequest request) throws PaymentNotDoneException {

		if (request == null) {
			throw new PaymentNotDoneException("Payment verification request is missing");
		}

		if (request.getCropDealOrderId() == null || request.getCropDealOrderId().isBlank()) {
			throw new PaymentNotDoneException("CropDeal order ID is missing");
		}

		if (request.getRazorpayOrderId() == null || request.getRazorpayOrderId().isBlank()) {
			throw new PaymentNotDoneException("Razorpay order ID is missing");
		}

		if (request.getRazorpayPaymentId() == null || request.getRazorpayPaymentId().isBlank()) {
			throw new PaymentNotDoneException("Razorpay payment ID is missing");
		}

		if (request.getRazorpaySignature() == null || request.getRazorpaySignature().isBlank()) {
			throw new PaymentNotDoneException("Razorpay signature is missing");
		}

		if (apiSecret == null || apiSecret.isBlank()) {
			throw new PaymentNotDoneException("Razorpay secret is not configured");
		}
	}

	private void verifySignature(Order order, PaymentVerificationRequest request) throws RazorpayException, PaymentNotDoneException {

		JSONObject attributes = new JSONObject();

		attributes.put("razorpay_order_id", order.getRazorpayOrderId());
		attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
		attributes.put("razorpay_signature", request.getRazorpaySignature());

		boolean validSignature = Utils.verifyPaymentSignature(attributes, apiSecret);

		if (!validSignature) {
			throw new PaymentNotDoneException("Razorpay payment signature verification failed");
		}

		log.info("Razorpay signature verified for payment {}", request.getRazorpayPaymentId());
	}

	private void validatePaymentOrder(Payment payment, Order order) throws PaymentNotDoneException {

		String razorpayPaymentOrderId = payment.get("order_id");

		if (razorpayPaymentOrderId == null || !razorpayPaymentOrderId.equals(order.getRazorpayOrderId())) {
			throw new PaymentNotDoneException("Razorpay payment belongs to a different order");
		}
	}

	private void validatePaymentAmount(Payment payment, Order order) throws PaymentNotDoneException {

		Object rawAmount = payment.get("amount");

		if (!(rawAmount instanceof Number)) {
			throw new PaymentNotDoneException("Invalid amount returned by Razorpay");
		}

		long paidAmount = ((Number) rawAmount).longValue();
		long expectedAmount = Math.round(order.getTotalPrice() * 100);

		if (paidAmount != expectedAmount) {
			throw new PaymentNotDoneException("Razorpay payment amount does not match the CropDeal order");
		}
	}

	private void validatePaymentCurrency(Payment payment) throws PaymentNotDoneException {

		String currency = payment.get("currency");

		if (currency == null || !"INR".equalsIgnoreCase(currency)) {
			throw new PaymentNotDoneException("Unexpected Razorpay payment currency");
		}
	}

	private Payment ensurePaymentCaptured(Payment payment, String paymentId, Order order) throws RazorpayException, PaymentNotDoneException {

		String paymentStatus = payment.get("status");

		log.info("Razorpay payment {} current status: {}", paymentId, paymentStatus);

		if ("authorized".equalsIgnoreCase(paymentStatus)) {

			log.info("Capturing authorized Razorpay payment {}", paymentId);

			long expectedAmount = Math.round(order.getTotalPrice() * 100);

			JSONObject captureRequest = new JSONObject();
			captureRequest.put("amount", expectedAmount);
			captureRequest.put("currency", "INR");

			payment = razorpayClient.payments.capture(paymentId, captureRequest);

			paymentStatus = payment.get("status");

			log.info("Razorpay payment {} status after capture: {}", paymentId, paymentStatus);
		}

		if (!"captured".equalsIgnoreCase(paymentStatus)) {
			throw new PaymentNotDoneException(
					"Payment has not been captured. Current status: " + paymentStatus
			);
		}

		return payment;
	}
}