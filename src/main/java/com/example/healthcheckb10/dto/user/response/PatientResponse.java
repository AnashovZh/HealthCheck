package com.example.healthcheckb10.dto.user.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PatientResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String date;
    public PatientResponse(Long id, String fullName, String phoneNumber, String email, String date) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.date = date;
    }
}