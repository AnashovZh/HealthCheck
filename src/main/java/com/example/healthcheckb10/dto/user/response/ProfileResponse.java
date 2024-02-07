package com.example.healthcheckb10.dto.user.response;

import com.example.healthcheckb10.entities.User;
import com.example.healthcheckb10.entities.UserAccount;

public record ProfileResponse(String firstName, String lastName, String email, String phoneNumber) {
    public static ProfileResponse build(User user, UserAccount userAccount) {
        return new ProfileResponse(user.getFirstName(), user.getLastName(),
                userAccount.getEmail(), user.getPhoneNumber());
    }
}