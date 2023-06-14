//package com.cropdeal.usermanagement.security;
//import java.io.IOException;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//
//public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
//
//    private final JwtTokenProvider tokenProvider;
//
//    public JwtAuthenticationFilter(String loginUrl, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
//        super(new AntPathRequestMatcher(loginUrl));
//        setAuthenticationManager(authenticationManager);
//        this.tokenProvider = tokenProvider;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
//        String token = tokenProvider.resolveToken(request);
//        if (token != null && tokenProvider.validateToken(token)) {
//            String username = tokenProvider.getUsernameFromToken(token);
//            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, token));
//        }
//        return null;
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        SecurityContextHolder.getContext().setAuthentication(authResult);
//        chain.doFilter(request, response);
//    }
//}
