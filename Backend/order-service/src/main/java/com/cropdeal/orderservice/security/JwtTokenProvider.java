package com.cropdeal.orderservice.security;

import java.util.List;
import java.util.stream.Collectors;

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
	
	private static final String key = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		String userId = (String) claims.get("username");
		@SuppressWarnings("unchecked")
		List<String> roles = (List<String>) claims.get("roles");

		UserDetails userDetails = User.builder().username(userId).password("") // Password is not needed for
																					// authentication
				.authorities(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())).build();

		return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
	}

	public String resolveToken(HttpServletRequest request) {
		log.info("Resolving bearer token : {}", request.getHeader("Authorization"));
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			log.info("Token resolved {}", bearerToken.substring(7));
			return bearerToken.substring(7);
		}
		log.info("Couldn't resolve token");
		return null;
	}

	public boolean validateToken(String token) {
		try {
			log.info("Validating token {}", token);
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			
			log.info("Token validated.");
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
}
