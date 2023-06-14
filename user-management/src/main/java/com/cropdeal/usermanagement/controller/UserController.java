package com.cropdeal.usermanagement.controller;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.usermanagement.entity.User;
import com.cropdeal.usermanagement.exception.UserAlreadyExistsException;
import com.cropdeal.usermanagement.jwtconfig.AuthenticationRequest;
import com.cropdeal.usermanagement.jwtconfig.JwtUtil;
import com.cropdeal.usermanagement.model.AuthResponse;
import com.cropdeal.usermanagement.model.DealerRegistrationRequest;
import com.cropdeal.usermanagement.model.FarmerRegistrationRequest;
import com.cropdeal.usermanagement.service.GroupUserDetailsService;
import com.cropdeal.usermanagement.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService service;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private GroupUserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@PostMapping("/register/farmer")
	public ResponseEntity<String> registerUser(@RequestBody FarmerRegistrationRequest user)
			throws UserAlreadyExistsException {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = service.registerFarmer(user);
		return ResponseEntity.ok("Farmer registered successfully with id : " + savedUser.getId());
	}

	@PostMapping("/register/dealer")
	public ResponseEntity<String> registerUser(@RequestBody DealerRegistrationRequest user)
			throws UserAlreadyExistsException {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = service.registerDealer(user);
		return ResponseEntity.ok("Dealer registered successfully with id : " + savedUser.getId());
	}

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		final String jwt = jwtTokenUtil.generateToken(userDetails);
		final long expireAt = jwtTokenUtil.extractExpiration(jwt).getTime();
		final Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		final Collection<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		AuthResponse authResponse = new AuthResponse(jwt, expireAt, authorityStrings);

		return ResponseEntity.ok(authResponse);
	}

	@DeleteMapping("/{id}")
	@Secured("ROLE_ADMIN")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> deleteUserById(@PathVariable("id") String id) {
		service.deleteById(id);
		return ResponseEntity.ok("User with ID: " + id + " deleted successfully");
	}
}
