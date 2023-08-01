package com.cropdeal.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegistrationRequest {
    private String username;
    private String password;
    private String role;
    private String gender;
    private String firstName;
    private String lastName;
    private long phoneNumber;
    private String email;
}

