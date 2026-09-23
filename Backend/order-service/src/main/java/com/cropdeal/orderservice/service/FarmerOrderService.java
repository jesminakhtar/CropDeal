package com.cropdeal.orderservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cropdeal.orderservice.dto.FarmerOrderResponse;
import com.cropdeal.orderservice.entity.Order;
import com.cropdeal.orderservice.model.Product;
import com.cropdeal.orderservice.model.Shop;
import com.cropdeal.orderservice.repository.OrderRepository;

@Service
public class FarmerOrderService {

    private static final Logger log = LoggerFactory.getLogger(FarmerOrderService.class);

    private static final String INVENTORY_SERVICE_URL = "http://inventory-service";

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;

    public FarmerOrderService(RestTemplate restTemplate, OrderRepository orderRepository) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
    }

    public List<FarmerOrderResponse> getFarmerOrders(String farmerUsername) {

        log.info("Fetching sales orders for farmer {}", farmerUsername);

        Set<String> shopIds = getFarmerShopIds(farmerUsername);

        if (shopIds.isEmpty()) {
            log.info("No shops found for farmer {}", farmerUsername);
            return List.of();
        }

        Set<String> farmerProductIds = getFarmerProductIds(shopIds);

        if (farmerProductIds.isEmpty()) {
            log.info("No products found for farmer {}", farmerUsername);
            return List.of();
        }

        List<Order> orders = orderRepository.findAll();

        List<FarmerOrderResponse> farmerOrders = new ArrayList<>();

        for (Order order : orders) {

            if (!"Placed".equalsIgnoreCase(order.getStatus())) {
                continue;
            }

            if (!"Done".equalsIgnoreCase(order.getPaymentStatus())) {
                continue;
            }

            if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                continue;
            }

            Map<String, Integer> farmerItems = new HashMap<>();

            for (Map.Entry<String, Integer> item : order.getOrderItems().entrySet()) {

                if (farmerProductIds.contains(item.getKey())) {
                    farmerItems.put(item.getKey(), item.getValue());
                }
            }

            if (farmerItems.isEmpty()) {
                continue;
            }

            FarmerOrderResponse response = new FarmerOrderResponse();

            response.setOrderId(order.getOrderId());
            response.setDealerId(order.getDealerId());
            response.setStatus(order.getStatus());
            response.setPaymentStatus(order.getPaymentStatus());
            response.setPaymentMode(order.getPaymentMode());
            response.setTransactionId(order.getTransactionId());
            response.setOrderItems(farmerItems);
            response.setDeliveryAddressId(order.getDeliveryAddressId());

            farmerOrders.add(response);
        }

        farmerOrders.sort(
                Comparator.comparing(FarmerOrderResponse::getOrderId).reversed()
        );

        log.info(
                "Found {} sales orders for farmer {}",
                farmerOrders.size(),
                farmerUsername
        );

        return farmerOrders;
    }

    private Set<String> getFarmerShopIds(String farmerUsername) {

        String url = INVENTORY_SERVICE_URL
                + "/shops/farmerUsername/"
                + farmerUsername;

        Shop[] shops = restTemplate.getForObject(
                url,
                Shop[].class
        );

        Set<String> shopIds = new HashSet<>();

        if (shops == null) {
            return shopIds;
        }

        for (Shop shop : shops) {

            if (shop.getId() != null && !shop.getId().isBlank()) {
                shopIds.add(shop.getId());
            }
        }

        return shopIds;
    }

    private Set<String> getFarmerProductIds(Set<String> shopIds) {

        String url = INVENTORY_SERVICE_URL + "/products";

        Product[] products = restTemplate.getForObject(
                url,
                Product[].class
        );

        Set<String> productIds = new HashSet<>();

        if (products == null) {
            return productIds;
        }

        for (Product product : products) {

            if (
                    product.getId() != null
                            && product.getShopId() != null
                            && shopIds.contains(product.getShopId())
            ) {
                productIds.add(product.getId());
            }
        }

        return productIds;
    }

    public FarmerOrderResponse getFarmerOrder(String farmerUsername, String orderId) {

        List<FarmerOrderResponse> farmerOrders = getFarmerOrders(farmerUsername);

        return farmerOrders.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found for this farmer"));
    }
}