package com.example.healthcheckb10.dto.auth;

import com.example.healthcheckb10.enums.Role;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token,
        String email,
        Role role,
        String fullName,
        String phoneNumber,
        Long id) {
}