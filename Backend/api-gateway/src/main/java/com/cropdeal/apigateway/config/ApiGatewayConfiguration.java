package com.cropdeal.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class ApiGatewayConfiguration {

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration corsConfig = new CorsConfiguration();

		corsConfig.addAllowedOrigin("http://localhost:4200");
		corsConfig.addAllowedOrigin("http://127.0.0.1:4200");

		corsConfig.addAllowedMethod("*");
		corsConfig.addAllowedHeader("*");

		UrlBasedCorsConfigurationSource source =
				new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", corsConfig);

		return new CorsWebFilter(source);
	}
	
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
	    return builder.routes()
	            .route("inventory-service", r -> r.path("/products/**", "/shops/**")
	                    .uri("lb://inventory-service"))
	            .route("order-service", r -> r.path("/orders/**", "/carts/**", "/receipts/**", "/payments/**", "/transactions/**")
	                    .uri("lb://order-service"))
	            .route("user-service", r -> r.path("/users/**", "/bank-accounts/**", "/address/**")
	                    .uri("lb://user-service"))
	            .build();
	}
}
