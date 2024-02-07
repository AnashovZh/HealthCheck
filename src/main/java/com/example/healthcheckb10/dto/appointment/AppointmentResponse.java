package com.example.healthcheckb10.dto.appointment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentResponse {
    Long id;
    LocalDate localDate;
    String time;
    String doctorFullName;
    String status;
    String position;

    public AppointmentResponse(Long id, LocalDate localDate, String time, String doctorFullName, String status, String position) {
        this.id = id;
        this.localDate = localDate;
        this.time = time;
        this.doctorFullName = doctorFullName;
        this.status = status;
        this.position = position;
    }
}
