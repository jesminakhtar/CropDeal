package com.cropdeal.usermanagement.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "BankAccounts")
public class BankAccount {
	@Id
    private String id;
    private String accountNumber;
    private String accountHolderName;
    private String bankName;
    private String ifscCode;
}
