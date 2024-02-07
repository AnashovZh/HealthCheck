package com.example.healthcheckb10.dto.appointment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentResponseForGetById {
    Long id;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    LocalDate localDate;
    String time;
    String doctorFullName;
    String departmentName;
    String status;

    public AppointmentResponseForGetById(Long id, String firstName, String lastName, String email, String phoneNumber, LocalDate localDate, String time, String doctorFullName, String departmentName, String status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.localDate = localDate;
        this.time = time;
        this.doctorFullName = doctorFullName;
        this.departmentName = departmentName;
        this.status = status;
    }
}
