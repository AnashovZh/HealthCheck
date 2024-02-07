package com.example.healthcheckb10.dto.auth;

import com.example.healthcheckb10.validation.MyEmailValidation;
import lombok.Getter;

@Getter
public class SignInRequest {
    @MyEmailValidation
    String email;
    String password;
}