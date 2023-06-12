package com.cropdeal.usermanagement.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cropdeal.usermanagement.entity.Role;
import com.cropdeal.usermanagement.entity.User;
import com.cropdeal.usermanagement.exception.UserAlreadyExistsException;
import com.cropdeal.usermanagement.exception.UserNotFoundException;
import com.cropdeal.usermanagement.model.DealerRegistrationRequest;
import com.cropdeal.usermanagement.model.FarmerRegistrationRequest;
import com.cropdeal.usermanagement.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
//	@Autowired 
//	private Client oktaClient;
	
	Logger logger = LoggerFactory.getLogger(UserService.class);

	   public User registerFarmer(FarmerRegistrationRequest registrationRequest) throws UserAlreadyExistsException {
	        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
	            throw new UserAlreadyExistsException("Email already exists");
	        }
	        
	        User user = new User();
			user.setEmail(registrationRequest.getEmail());
			user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
			user.setRole(Role.FARMER);

//			logger.info("OktaClient ======= ", oktaClient);
//	        
//	        // Create the user in Okta
//	        com.okta.sdk.resource.user.User oktaUser = UserBuilder.instance()
//	          .setEmail(registrationRequest.getEmail())
//	          .setPassword(passwordEncoder.encode(registrationRequest.getPassword()).toCharArray())
//	          .buildAndCreate(oktaClient);
//	          
//	        // Map the Okta user ID to your application's user record
//	        logger.info("OktaClient ======= ", oktaUser);
//	        user.setId(oktaUser.getId());
	        return userRepository.save(user);
	        
	    }

	public User registerDealer(DealerRegistrationRequest registrationRequest) throws UserAlreadyExistsException {
		if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        
        User user = new User();
		user.setEmail(registrationRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
		user.setRole(Role.DEALER);

        
//        // Create the user in Okta
//        com.okta.sdk.resource.user.User oktaUser = UserBuilder.instance()
//          .setEmail(registrationRequest.getEmail())
//          .setPassword(passwordEncoder.encode(registrationRequest.getPassword()).toCharArray())
//          .buildAndCreate(oktaClient);
//          
//        // Map the Okta user ID to your application's user record
//        user.setId(oktaUser.getId());
        return userRepository.save(user);
	}

	public User getUserById(String userId) throws UserNotFoundException {
		return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id : " + userId));
	}

	public void updateUser(String userId, User user) throws UserNotFoundException {
        User existingUser = getUserById(userId);

        // Update common fields
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        // Update additional common fields as needed

        userRepository.save(existingUser);
    }

	public void deleteUser(String userId) throws UserNotFoundException {
		User user = getUserById(userId);
		userRepository.delete(user);
	}

	public List<User> getUsers() {
		return userRepository.findAll();
	}
}
