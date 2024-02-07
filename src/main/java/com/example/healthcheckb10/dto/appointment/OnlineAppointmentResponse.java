package com.example.healthcheckb10.dto.appointment;

import com.example.healthcheckb10.enums.Day;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record OnlineAppointmentResponse(
        Day day,
        LocalDate date,
        String startTimeAndEndTime,
        String doctorFullName,
        String doctorImage,
        String departmentName,
        Long appointmentId) {
}