package com.cropdeal.usermanagement.controller;

import com.cropdeal.usermanagement.dto.AuthenticationRequest;
import com.cropdeal.usermanagement.dto.AuthenticationResponse;
import com.cropdeal.usermanagement.dto.RegistrationRequest;
import com.cropdeal.usermanagement.entity.User;
import com.cropdeal.usermanagement.exception.UserAlreadyExistsException;
import com.cropdeal.usermanagement.exception.UserNotFoundException;
import com.cropdeal.usermanagement.security.JwtTokenProvider;
import com.cropdeal.usermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);
        AuthenticationResponse loginResponse = new AuthenticationResponse(token);

        log.info("User '{}' successfully logged in.", loginRequest.getUsername());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegistrationRequest registrationRequest) throws UserAlreadyExistsException {
        User registeredUser = userService.registerUser(registrationRequest);
        log.info("User '{}' successfully registered.", registeredUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) throws UserNotFoundException {
        User user = userService.findUserByUsername(username);
        if (user != null) {
            log.info("User '{}' found.", username);
            return ResponseEntity.ok(user);
        } else {
            log.warn("User '{}' not found.", username);
            return ResponseEntity.notFound().build();
        }
    }
}
