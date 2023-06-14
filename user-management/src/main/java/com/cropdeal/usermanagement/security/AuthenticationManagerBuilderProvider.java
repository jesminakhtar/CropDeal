//package com.cropdeal.usermanagement.security;
//
//import com.cropdeal.usermanagement.service.UserDetailsServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class AuthenticationManagerBuilderProvider {
//
//    private AuthenticationManager authenticationManager;
//    private UserDetailsServiceImpl userDetailsService;
//
//    @Autowired
//    public AuthenticationManagerBuilderProvider(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
//        this.authenticationManager = authenticationManager;
//        this.userDetailsService = userDetailsService;
//    }
//
//    public Authentication authenticate(String username, String password) throws AuthenticationException {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(username, password)
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        return authentication;
//    }
//
//    public UserDetailsServiceImpl getUserDetailsService() {
//        return userDetailsService;
//    }
//}
