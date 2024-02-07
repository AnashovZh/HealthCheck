package com.example.healthcheckb10.dto.appointment.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FreeTimeSheet {
    private String startTimeConsultation;

    public FreeTimeSheet(String startTimeConsultation) {
        this.startTimeConsultation = startTimeConsultation;
    }
}
