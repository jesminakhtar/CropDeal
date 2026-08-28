package com.cropdeal.usermanagement.service;

import java.util.List;
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
import com.cropdeal.usermanagement.dto.EmailDetails;
import com.cropdeal.usermanagement.dto.RegistrationRequest;
import com.cropdeal.usermanagement.entity.Role;
import com.cropdeal.usermanagement.entity.User;
import com.cropdeal.usermanagement.exception.UserAlreadyExistsException;
import com.cropdeal.usermanagement.exception.UserNotFoundException;
//import com.cropdeal.usermanagement.messaging.RabbitMQSender;
import com.cropdeal.usermanagement.repository.UserRepository;
import com.cropdeal.usermanagement.security.JwtTokenProvider;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
//	private final RabbitMQSender rabbitMQSender;

	Logger log = LoggerFactory.getLogger(UserService.class);

	public UserService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider
//			RabbitMQSender rabbitMQSender
	) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
//		this.rabbitMQSender = rabbitMQSender;
	}

	public AuthenticationResponse loginUser(AuthenticationRequest loginRequest) throws UserNotFoundException {

		String userName = loginRequest.getUsername();

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(userName, loginRequest.getPassword()));
		String token = jwtTokenProvider.generateToken(authentication);
		User user = getUserByUsername(userName);

//		rabbitMQSender.sendLoginMessage("User logged in: " + userName);

		return new AuthenticationResponse(token, user.getId(), user.getUsername(), user.getRole(), user.getFirstName(),
				user.getLastName(), user.getEmail());
	}

	public User registerUser(RegistrationRequest registrationRequest) throws UserAlreadyExistsException {
		if (userRepository.existsByEmail(registrationRequest.getEmail())) {
			throw new UserAlreadyExistsException("Email already exists");
		}

//		// Generate OTP
//	    String otp = generateOtp();

		User user = new User();
		user.setEmail(registrationRequest.getEmail());
		user.setPhoneNumber(registrationRequest.getPhoneNumber());
		user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
		user.setFirstName(registrationRequest.getFirstName());
		user.setLastName(registrationRequest.getLastName());
		user.setUsername(registrationRequest.getUsername());
		user.setGender(registrationRequest.getGender());

		// Set the role based on the value provided in the RegistrationRequest
		String role = registrationRequest.getRole();
		if (Role.DEALER.name().equalsIgnoreCase(role)) {
			user.setRole(role.toUpperCase());
			user.setId("D" + generateUniqueId());
			log.info("Dealer id : {}", user.getId());
		} else if (Role.FARMER.name().equalsIgnoreCase(role)) {
			user.setRole(role.toUpperCase());
			user.setId("F" + generateUniqueId());
			log.info("Farmer id : {}", user.getId());
		}

//		rabbitMQSender.sendRegistrationMessage("Registration successful for " + user.getUsername());

		sendRegistrationSuccessEmail(registrationRequest.getEmail(), user.getId());

		return userRepository.save(user);
	}

	public User getUserByUsername(String username) throws UserNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found with id : " + username));
	}

	public void updateUser(String username, User user) throws UserNotFoundException {
		User existingUser = getUserByUsername(username);
		existingUser.setEmail(user.getEmail());
		existingUser.setPhoneNumber(user.getPhoneNumber());
		existingUser.setFirstName(user.getFirstName());
		existingUser.setLastName(user.getLastName());
		existingUser.setUsername(user.getUsername());
		existingUser.setGender(user.getGender());

		userRepository.save(existingUser);
	}

	public void deleteUser(String username) throws UserNotFoundException {
		User user = getUserByUsername(username);
		userRepository.delete(user);
	}

	public User findUserByUsername(String username) throws UserNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found with username : " + username));
	}

	private String generateUniqueId() {
		String uniqueId = UUID.randomUUID().toString();
		return uniqueId.replace("-", "").substring(0, 6);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public void sendRegistrationSuccessEmail(String recipientEmail, String username) {
		EmailDetails emailDetails = new EmailDetails();
		emailDetails.setRecipient(recipientEmail);
		emailDetails.setSubject("CropDeal Registration Successful");
		emailDetails.setMsgBody("Congratulations! Your registration was successful. Your username is : " + username);
		emailService.sendSimpleMail(emailDetails);
	}

//	private String generateOtp() {
//		
//		storedOtp = 
//		return storedOtp;
//	}
//	
//	public boolean verifyOtp(String email, String otp) {
//	    // You need to implement the logic to verify the OTP
//	    // Here, you can compare the OTP entered by the user with the stored OTP for the given email
//	    // Return true if the OTP is verified successfully, false otherwise
//	    // For example:
//	     // Get the stored OTP from a database or cache based on the email
//	    return otp.equals(storedOtp);
//	}

}
