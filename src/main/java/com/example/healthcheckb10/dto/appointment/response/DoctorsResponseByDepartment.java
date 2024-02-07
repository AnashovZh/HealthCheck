package com.example.healthcheckb10.dto.appointment.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DoctorsResponseByDepartment {
    private Long id;
    private String fullName;
    private String image;
    private String position;

    public DoctorsResponseByDepartment(Long id, String fullName, String image, String position) {
        this.id = id;
        this.fullName = fullName;
        this.image = image;
        this.position = position;
    }
}
