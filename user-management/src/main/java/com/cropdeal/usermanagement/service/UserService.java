package com.cropdeal.usermanagement.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cropdeal.usermanagement.dto.AuthenticationRequest;
import com.cropdeal.usermanagement.dto.AuthenticationResponse;
import com.cropdeal.usermanagement.dto.RegistrationRequest;
import com.cropdeal.usermanagement.entity.Role;
import com.cropdeal.usermanagement.entity.User;
import com.cropdeal.usermanagement.exception.UserAlreadyExistsException;
import com.cropdeal.usermanagement.exception.UserNotFoundException;
import com.cropdeal.usermanagement.messaging.MessageProducer;
import com.cropdeal.usermanagement.repository.UserRepository;
import com.cropdeal.usermanagement.security.JwtTokenProvider;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageProducer messageProducer;

    Logger log = LoggerFactory.getLogger(UserService.class);
    
    public UserService(AuthenticationManager authenticationManager , JwtTokenProvider jwtTokenProvider, MessageProducer messageProducer) {
    	this.authenticationManager = authenticationManager;
    	this.jwtTokenProvider = jwtTokenProvider;
    	this.messageProducer = messageProducer;
    }
	
	public AuthenticationResponse loginUser(AuthenticationRequest loginRequest) throws UserNotFoundException {
		
		String userName = loginRequest.getUsername();
		
		Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName, loginRequest.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);
        
        User user = getUserByUsername(userName);
        
        messageProducer.sendMessage("user_events_exchange", "login_event_routing_key", "User logged in: " + userName);
        
        return new AuthenticationResponse(token,user.getId(), user.getUsername(), user.getRole(), user.getName(), user.getEmail());
        

	}
	
	
	public User registerUser(RegistrationRequest registrationRequest) throws UserAlreadyExistsException {
	    if (userRepository.existsByEmail(registrationRequest.getEmail())) {
	        throw new UserAlreadyExistsException("Email already exists");
	    }

	    User user = new User();
	    user.setEmail(registrationRequest.getEmail());
	    user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
	    user.setName(registrationRequest.getName());
	    user.setUsername(registrationRequest.getUsername());
	    user.setBankAccount(registrationRequest.getBankAccount());
	    
	    // Set the role based on the value provided in the RegistrationRequest
	    String role = registrationRequest.getRole();
	    if (Role.DEALER.name().equalsIgnoreCase(role)) {
	    	user.setRole(role.toUpperCase());
	    	user.setId("D" + generateUniqueId());
	    	log.info("Dealer id : {}", user.getId());
	    }
	    else if (Role.FARMER.name().equalsIgnoreCase(role)) {
	    	user.setRole(role.toUpperCase());
	    	user.setId("F" + generateUniqueId());
	    	log.info("Farmer id : {}", user.getId());
	    }
	    
	    messageProducer.sendMessage("user_events_exchange", "registration_event_routing_key", "User registered: " + registrationRequest.getUsername());

	    return userRepository.save(user);
	}

	public User getUserByUsername(String username) throws UserNotFoundException {
		return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found with id : " + username));
	}

	public void updateUser(String username, User user) throws UserNotFoundException {
        User existingUser = getUserByUsername(username);
        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        existingUser.setUsername(user.getUsername());
        existingUser.setBankAccount(user.getBankAccount());
        
        userRepository.save(existingUser);
    }

	public void deleteUser(String username) throws UserNotFoundException {
		User user = getUserByUsername(username);
		userRepository.delete(user);
	}

	public User findUserByUsername(String username) throws UserNotFoundException {
		return userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User not found with username : " + username));
	}
	
	private String generateUniqueId() {
	    String uniqueId = UUID.randomUUID().toString();
	    return uniqueId.replace("-", "").substring(0,6);
	}

}
