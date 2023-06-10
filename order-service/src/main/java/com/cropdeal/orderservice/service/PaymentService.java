package com.cropdeal.orderservice.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropdeal.orderservice.exception.PaymentNotDoneException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class PaymentService {

    @Autowired
    private RazorpayClient razorpayClient;

    public String processPayment(double amount, String receiptId) throws PaymentNotDoneException {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receiptId);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            // Verify the payment status
            if (razorpayOrder != null && razorpayOrder.get("status").equals("created")) {
                // Payment is successful
                // You can retrieve the Razorpay order ID using razorpayOrder.get("id")
                return razorpayOrder.get("id").toString();
            } else {
                // Payment failed
                throw new PaymentNotDoneException("Payment processing failed for receipt: " + receiptId);
            }
        } catch (RazorpayException e) {
            throw new PaymentNotDoneException("Payment processing failed for receipt: " + receiptId);
        }
    }


    public boolean checkPaymentStatus(String razorpayOrderId) {
        try {
            // Fetch the payment order details from Razorpay
            Order razorpayOrder = razorpayClient.orders.fetch(razorpayOrderId);

            // Check the payment status
            return razorpayOrder != null && razorpayOrder.get("status").equals("paid");
        } catch (RazorpayException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void processPaymentAdjustment(String razorpayOrderId, double newAmount) throws PaymentNotDoneException {
//        try {
//            JSONObject orderRequest = new JSONObject();
//            orderRequest.put("amount", newAmount); // amount in the smallest currency unit
//
//            Order razorpayOrder = razorpayClient.orders.fetch(razorpayOrderId);
//
//            // Update the payment amount
//            razorpayOrder = razorpayOrder.edit(orderRequest);
//
//            if (razorpayOrder != null && razorpayOrder.get("status").equals("paid")) {
//                // Payment adjustment successful
//                return;
//            } else {
//                // Payment adjustment failed
//                throw new PaymentNotDoneException("Payment adjustment failed");
//            }
//        } catch (RazorpayException e) {
//            throw new PaymentNotDoneException("Payment adjustment failed");
//        }
    }

    public void processPaymentRefund(String razorpayOrderId) throws PaymentNotDoneException {
//        try {
//            Order razorpayOrder = razorpayClient.orders.fetch(razorpayOrderId);
//
//            // Initiate refund for the payment order
//            JSONObject refundRequest = new JSONObject();
//            refundRequest.put("amount", razorpayOrder.get("amount"));
//
//            razorpayClient.payments.refund(razorpayOrderId, refundRequest);
//        } catch (RazorpayException e) {
//            throw new PaymentNotDoneException("Payment refund failed");
//        }
    }

    // Additional methods or code for the PaymentService class can be added here

}
