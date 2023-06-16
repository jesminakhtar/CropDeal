package com.cropdeal.usermanagement.security;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final long TOKEN_VALIDITY = 86400000L; // 24 hours
    private static final String AUTHORITIES_KEY = "roles";
    private static final String key = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public String generateToken(Authentication authentication) {
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put(AUTHORITIES_KEY, userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("id", userDetails.getId());
        System.out.println("user id : " + userDetails.getId());
        // Add roles to claims
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + TOKEN_VALIDITY);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSignKey(),SignatureAlgorithm.HS256)
                .compact();

        log.debug("Generated JWT token for user '{}'", userDetails.getUsername());
        return token;
    }

    private Key getSignKey() {

        byte[] keyButes=Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyButes);

    }
    
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");

        UserDetails userDetails = User.builder()
                .username(username)
                .password("") // Password is not needed for authentication
                .authorities(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                .build();

        log.debug("Authenticated user '{}'", username);
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.debug("Valid JWT token");
            return true;
        } catch (Exception e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
