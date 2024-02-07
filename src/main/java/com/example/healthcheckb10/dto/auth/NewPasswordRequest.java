package com.example.healthcheckb10.dto.auth;

import com.example.healthcheckb10.validation.PasswordValidation;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NewPasswordRequest {
    @PasswordValidation
    private String newPassword;
    private String token;
}