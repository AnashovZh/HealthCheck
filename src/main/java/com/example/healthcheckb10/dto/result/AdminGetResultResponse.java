package com.example.healthcheckb10.dto.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class AdminGetResultResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String departmentName;
    private LocalDate dateOfUploadingResult;
    private String timeOfUploadingResult;
    private String resultNumber;
    private String pdgFileCheque;

    public AdminGetResultResponse(Long id, String firstName, String lastName, String email, String phoneNumber,
                                  String departmentName, LocalDate dateOfUploadingResult, String timeOfUploadingResult,
                                  String resultNumber, String pdgFileCheque) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.departmentName = departmentName;
        this.dateOfUploadingResult = dateOfUploadingResult;
        this.timeOfUploadingResult = timeOfUploadingResult;
        this.resultNumber = resultNumber;
        this.pdgFileCheque = pdgFileCheque;
    }
}