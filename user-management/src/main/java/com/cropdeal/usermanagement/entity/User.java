package com.cropdeal.usermanagement.entity;

import jakarta.validation.constraints.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Document(collection = "users")
public class User {
	@Id
	private String id;

	@NotEmpty(message = "Username is required")
	@Indexed(unique = true)
	private String username;

	@NotEmpty(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	private String password;

	@NotEmpty(message = "Role is required")
	private String role;

	@NotEmpty(message = "First name is required")
	private String firstName;

	@NotEmpty(message = "Last name is required")
	private String lastName;

	@NotEmpty(message = "Gender is required")
	private String gender;

	@NotEmpty(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotEmpty(message = "Phone number is required")
	private long phoneNumber;

	private List<Address> addresses;

	private List<BankAccount> bankAccounts;

	public User() {
		this.addresses = new ArrayList<>();
		this.bankAccounts = new ArrayList<>();
	}

	public User(@NotEmpty(message = "Username is required") String username,
			@NotEmpty(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") String password,
			@NotEmpty(message = "Role is required") String role,
			@NotEmpty(message = "First name is required") String firstName,
			@NotEmpty(message = "Last name is required") String lastName,
			@NotEmpty(message = "Gender is required") String gender,
			@NotEmpty(message = "Email is required") @Email(message = "Invalid email format") String email,
			@NotEmpty(message = "Phone number is required") long phoneNumber) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.addresses = new ArrayList<>();
		this.bankAccounts = new ArrayList<>();
	}
}
