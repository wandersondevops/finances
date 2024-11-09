package com.account.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "account_id", columnDefinition = "BINARY(16)")
    private UUID accountId;
    
    private Long accountNumber;
    
    private String accountType;
    
    private Double balance;
    
    private boolean status;
    
    private UUID clientId;

}
