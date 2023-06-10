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
    
    public String processPayment(double amount, String orderId) throws PaymentNotDoneException {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", orderId);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            // Verify the payment status
            if (razorpayOrder != null && razorpayOrder.get("status").equals("created")) {
                // Payment is successful
                // You can retrieve the Razorpay order ID using razorpayOrder.get("id")
                return razorpayOrder.get("id").toString();
            } else {
                // Payment failed
                throw new PaymentNotDoneException("Payment processing failed for order: " + orderId);
            }
        } catch (RazorpayException e) {
            throw new PaymentNotDoneException("Payment processing failed for order: " + orderId);
        }
    }


//    public void processPaymentAdjustment(Order oldOrder, Order updatedOrder) throws PaymentNotDoneException {
//        // Payment adjustment logic
//        // Implement the logic to handle payment adjustments in case of order updates
//    }
//
//    public void processPaymentRefund(Order order) {
//        // Payment refund logic
//        // Implement the logic to handle payment refunds if required
//    }
//
    public boolean checkPaymentStatus(String razorpayId) {
        try {
            // Fetch the payment order details from Razorpay
        	Order razorpayOrder = razorpayClient.orders.fetch(razorpayId);

            // Check the payment status
            return razorpayOrder != null && razorpayOrder.get("status").equals("paid");
        } catch (RazorpayException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Additional methods or code for the PaymentService class can be added here

}
