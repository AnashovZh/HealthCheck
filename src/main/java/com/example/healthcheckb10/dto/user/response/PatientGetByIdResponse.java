package com.example.healthcheckb10.dto.user.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PatientGetByIdResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    public PatientGetByIdResponse(Long id, String firstName, String lastName, String phoneNumber, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
