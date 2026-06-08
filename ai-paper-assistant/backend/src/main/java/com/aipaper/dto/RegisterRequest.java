package com.aipaper.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String username;

    private String password;

    private String email;

    private String grade;

    private String major;
}
