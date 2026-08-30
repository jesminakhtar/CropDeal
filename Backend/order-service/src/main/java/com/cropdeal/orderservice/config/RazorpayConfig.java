package com.cropdeal.orderservice.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Bean
    public RazorpayClient razorpayClient()
            throws RazorpayException {

        System.out.println("========== RAZORPAY CONFIG ==========");

        System.out.println(
                "Key starts with rzp_test_: " +
                        (apiKey != null &&
                                apiKey.startsWith("rzp_test_"))
        );

        System.out.println(
                "Secret present: " +
                        (apiSecret != null && !apiSecret.isBlank())
        );

        System.out.println(
                "Key has leading/trailing whitespace: " +
                        (apiKey != null &&
                                !apiKey.equals(apiKey.trim()))
        );

        System.out.println(
                "Secret has leading/trailing whitespace: " +
                        (apiSecret != null &&
                                !apiSecret.equals(apiSecret.trim()))
        );

        System.out.println("=====================================");

        return new RazorpayClient(
                apiKey,
                apiSecret
        );
    }
}