package com.example.healthcheckb10.dto.doctor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class DoctorResponse{
    private Long id;
    private String firstName;
    private String lastName;
    private String image;
    private String position;
    private Boolean isActive;
    private String departmentName;
    private LocalDate scheduleUntil;
    private String description;

    public DoctorResponse(Long id, String firstName, String lastName, String image, String position, Boolean isActive, String departmentName, LocalDate scheduleUntil, String description) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.position = position;
        this.isActive = isActive;
        this.departmentName = departmentName;
        this.scheduleUntil = scheduleUntil;
        this.description = description;
    }
}