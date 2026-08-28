package com.cropdeal.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String userId;
    private String username;
    private String role;
    private String firstName;
    private String lastName;
    private String email;

    
   
}

