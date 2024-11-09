package com.account.entity;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class AccountAggregate {

    // Client details
    private UUID clientId;
    private String address;
    private Integer age;
    private String gender;
    private Long id;
    private String identification;
    private String name;
    private String phone;
    private Boolean clientStatus;

    // Account details
    private UUID accountId;
    private Long accountNumber;
    private String accountType;
    private Double balance;
    private Boolean accountStatus;


    private List<Transaction> transactions;

}
