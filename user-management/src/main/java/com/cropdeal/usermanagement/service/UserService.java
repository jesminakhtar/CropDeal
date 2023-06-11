package com.cropdeal.usermanagement.service;

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

	public void registerFarmer(FarmerRegistrationRequest registrationRequest) throws UserAlreadyExistsException {
		if (userRepository.existsByEmail(registrationRequest.getEmail())) {
			throw new UserAlreadyExistsException("Email already exists");
		}

		User user = new User();
		user.setEmail(registrationRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
		user.setRole(Role.FARMER);
		// Set other properties of the user entity
		// ...

		userRepository.save(user);
	}

	public void registerDealer(DealerRegistrationRequest registrationRequest) throws UserAlreadyExistsException {
		if (userRepository.existsByEmail(registrationRequest.getEmail())) {
			throw new UserAlreadyExistsException("Email already exists");
		}

		User user = new User();
		user.setEmail(registrationRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
		user.setRole(Role.DEALER);
		// Set other properties of the user entity
		// ...

		userRepository.save(user);
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
}
