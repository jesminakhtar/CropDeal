package com.cropdeal.inventoryservice.security;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {
	
	private static final String KEY = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
		
		String userId = (String) claims.get("username");
		log.info("Userid : {}", userId);
		@SuppressWarnings("unchecked")
		List<String> roles = (List<String>) claims.get("roles");

		UserDetails userDetails = User.builder().username(userId).password("") // Password is not needed for
																					// authentication
				.authorities(roles.stream().map(SimpleGrantedAuthority::new).toList()).build();

		return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			log.info("Token : {}", bearerToken.substring(7));
			return bearerToken.substring(7);
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
			
			log.info("Validated token : {}", token);
			return true;
		} catch (Exception e) {
			log.info(e.getMessage());
			return false;
		}
	}
}
