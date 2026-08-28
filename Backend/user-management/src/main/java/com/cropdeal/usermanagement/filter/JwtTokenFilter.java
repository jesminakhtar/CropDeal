package com.cropdeal.usermanagement.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cropdeal.usermanagement.security.JwtTokenProvider;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Get token from request header
            String token = getTokenFromRequest(request);
            // Validate token
            if (token != null && jwtTokenProvider.validateToken(token)) {
            	// Get user details from token and set authentication in the security context
                Authentication authentication = jwtTokenProvider.getAuthentication(token);             
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authenticated user '{}'", authentication.getName());
            }
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            // Handle authentication exception if needed
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
    	log.info("request : {}", request);
        String bearerToken = request.getHeader("Authorization");
        log.info("bearerToken : {}", bearerToken );
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
        	log.info("bearerToken.substring(7) : {}", bearerToken.substring(7) );
            return bearerToken.substring(7);
        }
        return null;
    }

}
