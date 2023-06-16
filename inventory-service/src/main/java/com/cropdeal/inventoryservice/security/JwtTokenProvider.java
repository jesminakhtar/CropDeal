package com.cropdeal.inventoryservice.security;

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

@Component
public class JwtTokenProvider {

//	private static final long TOKEN_VALIDITY = 86400000L; // 24 hours
//	private static final String AUTHORITIES_KEY = "roles";
	
	private static final String key = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

//		String username = claims.getSubject();
		String userId = (String) claims.get("id");
		System.out.println("Userid : " + userId);
		@SuppressWarnings("unchecked")
		List<String> roles = (List<String>) claims.get("roles");

		UserDetails userDetails = User.builder().username(userId).password("") // Password is not needed for
																					// authentication
				.authorities(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())).build();

		return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			System.out.println("Token : " + bearerToken.substring(7));
			return bearerToken.substring(7);
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			System.out.println("Validatkion...... token : " + token);
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//			Claims claim =  claims.getBody();
//			claim.getSubject();
			
			
			System.out.println("...... token : " + token);
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
}
