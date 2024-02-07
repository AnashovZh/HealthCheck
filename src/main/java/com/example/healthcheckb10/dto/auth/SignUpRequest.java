package com.example.healthcheckb10.dto.auth;

import com.example.healthcheckb10.validation.MyEmailValidation;
import com.example.healthcheckb10.validation.PhoneNumberValidation;
import lombok.Getter;

@Getter
public class SignUpRequest {
    String firstName;
    String lastName;
    @MyEmailValidation
    String email;
    @PhoneNumberValidation
    String phoneNumber;
    String password;
}