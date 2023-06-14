package com.cropdeal.usermanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.cropdeal.usermanagement.entity.User;
import com.cropdeal.usermanagement.exception.UserNotFoundException;
import com.cropdeal.usermanagement.model.GroupUserDetails;
import com.cropdeal.usermanagement.repository.UserRepository;

@Service
public class GroupUserDetailsService implements UserDetailsService {

	@Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) {
    	User user = null;
		try {
			user = repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("No user found with email: " + email));
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new GroupUserDetails(user);
    }

}
