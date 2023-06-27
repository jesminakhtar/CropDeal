package com.cropdeal.usermanagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cropdeal.usermanagement.dto.RegistrationRequest;
import com.cropdeal.usermanagement.entity.BankAccount;
import com.cropdeal.usermanagement.entity.Role;
import com.cropdeal.usermanagement.entity.User;
import com.cropdeal.usermanagement.exception.UserAlreadyExistsException;
import com.cropdeal.usermanagement.exception.UserNotFoundException;
import com.cropdeal.usermanagement.repository.UserRepository;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Logger log;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_WithNonExistingEmail_ShouldReturnRegisteredUser() throws UserAlreadyExistsException {
        // Arrange
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setName("John Doe");
        registrationRequest.setUsername("johndoe");

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234567890");
        bankAccount.setAccountHolderName("John Doe");
        bankAccount.setBankName("Bank of Example");
        bankAccount.setIfscCode("ABC123");

        registrationRequest.setBankAccount(bankAccount);

        registrationRequest.setRole(Role.DEALER.name());

        User mockUser = new User();
        mockUser.setEmail(registrationRequest.getEmail());
        mockUser.setPassword(registrationRequest.getPassword());
        mockUser.setName(registrationRequest.getName());
        mockUser.setUsername(registrationRequest.getUsername());
        mockUser.setBankAccount(registrationRequest.getBankAccount());
        mockUser.setRole(registrationRequest.getRole().toUpperCase());
        mockUser.setId("D123456");

        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn(registrationRequest.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User registeredUser = userService.registerUser(registrationRequest);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(registrationRequest.getEmail(), registeredUser.getEmail());
        assertEquals(registrationRequest.getName(), registeredUser.getName());
        assertEquals(registrationRequest.getUsername(), registeredUser.getUsername());
        assertEquals(registrationRequest.getBankAccount(), registeredUser.getBankAccount());
        assertEquals(registrationRequest.getRole().toUpperCase(), registeredUser.getRole());
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowUserAlreadyExistsException() {
        // Arrange
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("test@example.com");
        // ...

        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

        // Act and Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(registrationRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_WithExistingUserId_ShouldReturnUser() throws UserNotFoundException {
        // Arrange
        String userId = "D123456";
        User mockUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        User resultUser = userService.getUserByUsername(userId);

        // Assert
        assertNotNull(resultUser);
        assertEquals(mockUser, resultUser);
    }

    @Test
    void getUserById_WithNonExistingUserId_ShouldThrowUserNotFoundException() {
        // Arrange
        String userId = "D123456";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername(userId));
    }

    @Test
    void updateUser_WithExistingUserId_ShouldUpdateUser() throws UserNotFoundException {
        // Arrange
        String userId = "D123456";
        User existingUser = new User();
        User updatedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        // Act
        userService.updateUser(userId, updatedUser);

        // Assert
        verify(userRepository).save(existingUser);
        assertEquals(updatedUser.getEmail(), existingUser.getEmail());
        assertEquals(updatedUser.getName(), existingUser.getName());
        assertEquals(updatedUser.getUsername(), existingUser.getUsername());
        assertEquals(updatedUser.getBankAccount(), existingUser.getBankAccount());
    }

    @Test
    void updateUser_WithNonExistingUserId_ShouldThrowUserNotFoundException() {
        // Arrange
        String userId = "D123456";
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WithExistingUserId_ShouldDeleteUser() throws UserNotFoundException {
        // Arrange
        String userId = "D123456";
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_WithNonExistingUserId_ShouldThrowUserNotFoundException() {
        // Arrange
        String userId = "D123456";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void findUserByUsername_WithExistingUsername_ShouldReturnUser() throws UserNotFoundException {
        // Arrange
        String username = "johndoe";
        User mockUser = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        User resultUser = userService.findUserByUsername(username);

        // Assert
        assertNotNull(resultUser);
        assertEquals(mockUser, resultUser);
    }

    @Test
    void findUserByUsername_WithNonExistingUsername_ShouldThrowUserNotFoundException() {
        // Arrange
        String username = "johndoe";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.findUserByUsername(username));
    }

}
