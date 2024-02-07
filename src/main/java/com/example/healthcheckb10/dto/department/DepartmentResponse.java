package com.example.healthcheckb10.dto.department;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DepartmentResponse {
    private Long id;
    private String facilityName;
    public DepartmentResponse(Long id, String facilityName) {
        this.id = id;
        this.facilityName = facilityName;
    }
}