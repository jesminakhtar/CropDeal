package com.cropdeal.usermanagement.dto;

import com.cropdeal.usermanagement.entity.BankAccount;

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
    private String name;
    private String email;
    private BankAccount bankAccount;

}

