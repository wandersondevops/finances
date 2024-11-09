package com.account.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class ClientDTO {
    private UUID clientId;
    private String address;
    private Integer age;
    private String gender;
    private Long id;
    private String identification;
    private String name;
    private String phone;
    private Boolean status;
    private String type;
}

