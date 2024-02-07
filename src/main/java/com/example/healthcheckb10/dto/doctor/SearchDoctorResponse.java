package com.example.healthcheckb10.dto.doctor;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Builder
public class SearchDoctorResponse {
    private Long id;
    private Boolean isActive;
    private String image;
    private String firstName;
    private String lastName;
    private String position;
    private String departmentName;
    private String scheduleUntil;

    public SearchDoctorResponse(Long id, Boolean isActive, String image, String firstName, String lastName, String position, String departmentName, String scheduleUntil) {
        this.id = id;
        this.isActive = isActive;
        this.image = image;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.departmentName = departmentName;
        this.scheduleUntil = scheduleUntil;
    }
}