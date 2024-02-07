package com.example.healthcheckb10.dto.appointment.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class DoctorWithFreeTimsheet {
    private Long id;
    private String fullName;
    private String image;
    private String position;
    private String description;
    private List<String> freeTimesheets;

    public DoctorWithFreeTimsheet(Long id, String fullName, String image, String position, String description, List<String> freeTimesheets) {
        this.id = id;
        this.fullName = fullName;
        this.image = image;
        this.position = position;
        this.description = description;
        this.freeTimesheets = freeTimesheets;
    }
}