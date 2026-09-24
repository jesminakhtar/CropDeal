package com.cropdeal.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class ApiGatewayConfiguration {

	@Value("${frontend.url}")
	private String frontendUrl;

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration corsConfig = new CorsConfiguration();

		corsConfig.setAllowedOrigins(List.of(frontendUrl));
		corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		corsConfig.setAllowedHeaders(List.of("*"));
		corsConfig.setExposedHeaders(List.of("Authorization"));
		corsConfig.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);

		return new CorsWebFilter(source);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("inventory-service", r -> r
						.path("/products/**", "/shops/**")
						.uri("lb://inventory-service"))

				.route("order-service", r -> r
						.path(
								"/orders/**",
								"/carts/**",
								"/receipts/**",
								"/payments/**",
								"/transactions/**"
						)
						.uri("lb://order-service"))

				.route("user-service", r -> r
						.path(
								"/users/**",
								"/bank-accounts/**",
								"/address/**"
						)
						.uri("lb://user-service"))

				.build();
	}
}