package com.cropdeal.orderservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cropdeal.orderservice.security.JwtTokenFilter;
import com.cropdeal.orderservice.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	@Autowired
    private JwtTokenProvider jwtTokenProvider;

	@Bean
	public SecurityFilterChain filterChain(
			HttpSecurity security) throws Exception {

		security
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/orders/**").permitAll()
						.requestMatchers("/carts/**").permitAll()
						.requestMatchers("/receipts/**").permitAll()
						.requestMatchers("/payments/**").permitAll()
						.requestMatchers("/transactions/**").permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers(
								"/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html"
						).permitAll()
						.anyRequest().authenticated()
				)
				.addFilterBefore(
						jwtTokenFilter(),
						UsernamePasswordAuthenticationFilter.class
				);

		return security.build();
	}
	@Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtTokenProvider);
    }

}
