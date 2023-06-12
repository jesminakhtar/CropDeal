package com.cropdeal.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf().disable()
            .authorizeExchange()
                .pathMatchers("/users/register/**").permitAll()
                .pathMatchers("/users/register").permitAll() // Allow anonymous access to registration path
                .anyExchange().authenticated()
            .and()
                .oauth2Login()
            .and()
                .oauth2ResourceServer()
                .jwt();

        return http.build();
    }
}
