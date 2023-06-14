package com.cropdeal.usermanagement.service;

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
	
	public User registerUser(RegistrationRequest registrationRequest) throws UserAlreadyExistsException {
	    if (userRepository.existsByEmail(registrationRequest.getEmail())) {
	        throw new UserAlreadyExistsException("Email already exists");
	    }

	    User user = new User();
	    user.setEmail(registrationRequest.getEmail());
	    user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
	    user.setName(registrationRequest.getName());
	    user.setUsername(registrationRequest.getUsername());
	    
	    // Set the role based on the value provided in the RegistrationRequest
	    String role = registrationRequest.getRole();
	    if (Role.DEALER.name().equalsIgnoreCase(role) || Role.FARMER.name().equalsIgnoreCase(role)) {
	        user.setRole(role.toUpperCase());
	    } else {
	        // Default role if no valid role is provided
	        user.setRole(Role.FARMER.name());
	    }

	    return userRepository.save(user);
	}

	public User getUserById(String userId) throws UserNotFoundException {
		return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id : " + userId));
	}

	public void updateUser(String userId, User user) throws UserNotFoundException {
        User existingUser = getUserById(userId);

        // Update common fields
        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        existingUser.setUsername(user.getUsername());
        
        userRepository.save(existingUser);
    }

	public void deleteUser(String userId) throws UserNotFoundException {
		User user = getUserById(userId);
		userRepository.delete(user);
	}

	public User findUserByUsername(String username) throws UserNotFoundException {
		// TODO Auto-generated method stub
		return userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User not found with username : " + username));
	}
}
