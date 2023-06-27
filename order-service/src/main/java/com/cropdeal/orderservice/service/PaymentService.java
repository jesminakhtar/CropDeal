//package com.cropdeal.orderservice.service;
//
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.cropdeal.orderservice.exception.PaymentNotDoneException;
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//
//@Service
//public class PaymentService {
//
//	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
//
//	@Autowired
//	private RazorpayClient razorpayClient;
//
//	public String processPayment(double amount, String receiptId) throws PaymentNotDoneException {
//		try {
//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("amount", amount); // amount in the smallest currency unit
//			orderRequest.put("currency", "INR");
//			orderRequest.put("receipt", receiptId);
//
//			Order razorpayOrder = razorpayClient.orders.create(orderRequest);
//
//			// Verify the payment status
//			if (razorpayOrder != null && razorpayOrder.get("status").equals("created")) {
//				// Payment is successful
//				String razorpayOrderId = razorpayOrder.get("id").toString();
//				log.info("Payment processed successfully. Razorpay Order ID: {}", razorpayOrderId);
//				return razorpayOrderId;
//			} else {
//				// Payment failed
//				throw new PaymentNotDoneException("Payment processing failed for receipt: " + receiptId);
//			}
//		} catch (RazorpayException e) {
//			log.error("Payment processing failed for receipt: " + receiptId, e);
//			throw new PaymentNotDoneException("Payment processing failed for receipt: " + receiptId);
//		}
//	}
//
//	public boolean checkPaymentStatus(String razorpayOrderId) {
//		try {
//			// Fetch the payment order details from Razorpay
//			Order razorpayOrder = razorpayClient.orders.fetch(razorpayOrderId);
//
//			// Check the payment status
//			boolean isPaid = razorpayOrder != null && razorpayOrder.get("status").equals("paid");
//			log.info("Payment status for Razorpay Order ID {}: {}", razorpayOrderId, isPaid ? "Paid" : "Not Paid");
//			return isPaid;
//		} catch (RazorpayException e) {
//			log.error("Error checking payment status for Razorpay Order ID: " + razorpayOrderId, e);
//			return false;
//		}
//	}
//
//    public void processPaymentAdjustment(String razorpayOrderId, double newAmount) throws PaymentNotDoneException {
////        try {
////            JSONObject orderRequest = new JSONObject();
////            orderRequest.put("amount", newAmount); // amount in the smallest currency unit
////
////            Order razorpayOrder = razorpayClient.orders.fetch(razorpayOrderId);
////
////            // Update the payment amount
////            razorpayOrder = razorpayOrder.edit(orderRequest);
////
////            if (razorpayOrder != null && razorpayOrder.get("status").equals("paid")) {
////                // Payment adjustment successful
////                return;
////            } else {
////                // Payment adjustment failed
////                throw new PaymentNotDoneException("Payment adjustment failed");
////            }
////        } catch (RazorpayException e) {
////            throw new PaymentNotDoneException("Payment adjustment failed");
////        }
//    }
//
//    public void processPaymentRefund(String razorpayOrderId) throws PaymentNotDoneException {
////        try {
////            Order razorpayOrder = razorpayClient.orders.fetch(razorpayOrderId);
////
////            // Initiate refund for the payment order
////            JSONObject refundRequest = new JSONObject();
////            refundRequest.put("amount", razorpayOrder.get("amount"));
////
////            razorpayClient.payments.refund(razorpayOrderId, refundRequest);
////        } catch (RazorpayException e) {
////            throw new PaymentNotDoneException("Payment refund failed");
////        }
//    }
//
//    // Additional methods or code for the PaymentService class can be added here
//
//}
