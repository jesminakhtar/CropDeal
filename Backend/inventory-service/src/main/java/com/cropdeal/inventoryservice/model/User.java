package com.cropdeal.inventoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	private String id;
	private String username;
	private String password;
	private String role;
	private String firstName;
	private String lastName;
	private String gender;
	private String email;
	private long phoneNumber;
}
