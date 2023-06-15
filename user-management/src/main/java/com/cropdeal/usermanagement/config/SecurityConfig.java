package com.cropdeal.usermanagement.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cropdeal.usermanagement.filter.JwtTokenFilter;
import com.cropdeal.usermanagement.security.JwtTokenProvider;
import com.cropdeal.usermanagement.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {    	
    	
    	http
    		.csrf().disable()
    		.cors().and()
        	.authorizeRequests()
		        .antMatchers("/api/login").permitAll()
		        .antMatchers("/api/register").permitAll()
		        .anyRequest().authenticated()
		        .and()
	        // Add JWT token filter before each request
	        .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
	        // Configure exception handling
	        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler())
	        .and()
	        // Configure session management
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    	return http.build();
    }
    
    
    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtTokenProvider);
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return (request, response, e) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
