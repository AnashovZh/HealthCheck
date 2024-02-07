package com.example.healthcheckb10.dto.appointment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class OnlineAppointmentRequest {
    @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
    private String departmentName;
    @NotNull(message = "Поле не может быть пустым!")
    private Long doctorId;
    @NotNull(message = "Поле не может быть пустым!")
    private LocalDate dateOfVisiting;
    @NotNull(message = "Поле не может быть пустым!")
    private String timeOfVisiting;
    @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
    private String userFullName;
    @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
    private String userPhoneNumber;
    @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
    private String userEmail;
    @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
    private  String smsCode;


    public OnlineAppointmentRequest(String departmentName, Long doctorId, LocalDate dateOfVisiting,
                                    String timeOfVisiting, String userFullName, String userPhoneNumber,
                                    String userEmail ,String smsCode) {
        this.departmentName = departmentName;
        this.doctorId = doctorId;
        this.dateOfVisiting = dateOfVisiting;
        this.timeOfVisiting = timeOfVisiting;
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
        this.smsCode=smsCode;
    }
}