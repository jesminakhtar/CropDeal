package com.cropdeal.usermanagement.service;

import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cropdeal.usermanagement.dto.RegistrationRequest;
import com.cropdeal.usermanagement.entity.Role;
import com.cropdeal.usermanagement.entity.User;
import com.cropdeal.usermanagement.exception.UserAlreadyExistsException;
import com.cropdeal.usermanagement.exception.UserNotFoundException;
import com.cropdeal.usermanagement.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	Logger log = LoggerFactory.getLogger(UserService.class);
	
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

	    return userRepository.save(user);
	}

	public User getUserById(String userId) throws UserNotFoundException {
		return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id : " + userId));
	}

	public void updateUser(String userId, User user) throws UserNotFoundException {
        User existingUser = getUserById(userId);
        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        existingUser.setUsername(user.getUsername());
        existingUser.setBankAccount(user.getBankAccount());
        
        userRepository.save(existingUser);
    }

	public void deleteUser(String userId) throws UserNotFoundException {
		User user = getUserById(userId);
		userRepository.delete(user);
	}

	public User findUserByUsername(String username) throws UserNotFoundException {
		return userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User not found with username : " + username));
	}
	
	private String generateUniqueId() {
	    String uniqueId = UUID.randomUUID().toString();
	    return uniqueId.replaceAll("-", "").substring(0,6);
	}


}
