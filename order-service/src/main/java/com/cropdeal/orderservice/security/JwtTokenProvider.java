//package com.cropdeal.orderservice.security;
//
//import java.security.Key;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import jakarta.servlet.http.HttpServletRequest;
//
//@Component
//public class JwtTokenProvider {
//
////    private static final String SECRET_KEY = "your-secret-key";
////    private static final long TOKEN_VALIDITY = 86400000L; // 24 hours
//	
//	private static final String AUTHORITIES_KEY = "roles";
//
//    private Key key;
//
//    @PostConstruct
//    public void init() {
//        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    }
//
////    public String generateToken(Authentication authentication) {
////        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
////
////        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
////        claims.put("roles", userDetails.getAuthorities().stream()
////                .map(GrantedAuthority::getAuthority)
////                .collect(Collectors.toList()));
////
////        Date now = new Date();
////        Date validity = new Date(now.getTime() + TOKEN_VALIDITY);
////
////        return Jwts.builder()
////                .setClaims(claims)
////                .setIssuedAt(now)
////                .setExpiration(validity)
////                .signWith(key, SignatureAlgorithm.HS256)
////                .compact();
////    }
//
//
////    public Authentication getAuthentication(String token) {
////        Claims claims = Jwts.parserBuilder()
////                .setSigningKey(key)
////                .build()
////                .parseClaimsJws(token)
////                .getBody();
////
////        String username = claims.getSubject();
////        List<String> roles = (List<String>) claims.get("roles");
////
////        UserDetails userDetails = User.builder()
////                .username(username)
////                .password("") // Password is not needed for authentication
////                .authorities(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
////                .build();
////
////        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
////    }
//    
//    public Authentication getAuthentication(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        String username = claims.getSubject();
//        String role = (String) claims.get(AUTHORITIES_KEY);
//
//        UserDetails userDetails = User.builder()
//                .username(username)
//                .password("") // Password is not needed for authentication
//                .authorities(new SimpleGrantedAuthority(role))
//                .build();
//
//        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
//    }
//
//
//    public String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}
