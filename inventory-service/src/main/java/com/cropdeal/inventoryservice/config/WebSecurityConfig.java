package com.cropdeal.inventoryservice.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.cropdeal.inventoryservice.security.JwtTokenFilter;
import com.cropdeal.inventoryservice.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
    private JwtTokenProvider jwtTokenProvider;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
	    security
	        .csrf().disable()
	        .cors().and()
	        .authorizeHttpRequests()
	        .requestMatchers("/products").permitAll()
	        .requestMatchers("/products/findById/**").permitAll()
	        .requestMatchers("/products/*/updateQuantity").permitAll()
	        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
	        .anyRequest().authenticated()
	        .and()
	        .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	    return security.build();
	}

	
	@Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtTokenProvider);
    }
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	

}
