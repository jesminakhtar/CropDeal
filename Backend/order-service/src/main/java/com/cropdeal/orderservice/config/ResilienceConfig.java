package com.cropdeal.orderservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Set the failure rate threshold in percentage
                .waitDurationInOpenState(Duration.ofSeconds(5)) // Set the wait duration in open state
                .permittedNumberOfCallsInHalfOpenState(3) // Set the permitted number of calls in half-open state
                .slidingWindowSize(5) // Set the sliding window size
                .build();
    }

    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3) // Set the maximum number of retry attempts
                .waitDuration(Duration.ofMillis(500)) // Set the wait duration between retries
                .build();
    }
}
