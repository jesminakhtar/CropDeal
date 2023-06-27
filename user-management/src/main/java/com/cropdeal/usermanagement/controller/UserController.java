package com.cropdeal.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.usermanagement.dto.AuthenticationRequest;
import com.cropdeal.usermanagement.dto.AuthenticationResponse;
import com.cropdeal.usermanagement.dto.RegistrationRequest;
import com.cropdeal.usermanagement.entity.User;
import com.cropdeal.usermanagement.exception.UserAlreadyExistsException;
import com.cropdeal.usermanagement.exception.UserNotFoundException;
import com.cropdeal.usermanagement.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

	@Autowired
    private UserService userService;
    
//    private final UserEventProducer userEventProducer;


    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest loginRequest) throws UserNotFoundException {
        
    	log.info("Trying to login {}", loginRequest);
    	AuthenticationResponse loginResponse = userService.loginUser(loginRequest);
    	
        log.info("User '{}' successfully logged in.", loginRequest.getUsername());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<User> registerUser(@RequestBody RegistrationRequest registrationRequest) throws UserAlreadyExistsException {
        
    	log.info("Trying to register {}", registrationRequest);
    	User registeredUser = userService.registerUser(registrationRequest);
    	
//        userEventProducer.sendRegistrationEvent(registrationRequest.getUsername());
        
        log.info("User '{}' successfully registered.", registeredUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping("/{username}")
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
