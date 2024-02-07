package com.example.healthcheckb10.dto.appointment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SearchResponse {
    private Long id;
    private String patientFullName;
    private String phoneNumber;
    private String email;
    private String position;
    private String doctorFullName;
    private String dateAndTime;
    private String status;

    public SearchResponse(Long id, String patientFullName, String phoneNumber, String email, String position, String doctorFullName, String dateAndTime, String status) {
        this.id = id;
        this.patientFullName = patientFullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.position = position;
        this.doctorFullName = doctorFullName;
        this.dateAndTime = dateAndTime;
        this.status = status;
    }
}