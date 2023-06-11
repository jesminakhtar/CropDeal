package com.cropdeal.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.usermanagement.entity.User;
import com.cropdeal.usermanagement.exception.UserAlreadyExistsException;
import com.cropdeal.usermanagement.exception.UserNotFoundException;
import com.cropdeal.usermanagement.model.DealerRegistrationRequest;
import com.cropdeal.usermanagement.model.FarmerRegistrationRequest;
import com.cropdeal.usermanagement.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register/farmer")
    public ResponseEntity<String> registerFarmer(@RequestBody FarmerRegistrationRequest registrationRequest) {
        try {
            userService.registerFarmer(registrationRequest);
            return ResponseEntity.ok("Farmer registered successfully");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register/dealer")
    public ResponseEntity<String> registerDealer(@RequestBody DealerRegistrationRequest registrationRequest) {
        try {
            userService.registerDealer(registrationRequest);
            return ResponseEntity.ok("Dealer registered successfully");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable String userId, @RequestBody User updatedUser) {
        try {
            userService.updateUser(userId, updatedUser);
            return ResponseEntity.ok("User updated successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
