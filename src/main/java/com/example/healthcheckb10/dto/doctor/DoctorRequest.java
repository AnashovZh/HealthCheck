package com.example.healthcheckb10.dto.doctor;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DoctorRequest(
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String firstName,
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String lastName,
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String image,
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String position,
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String description) {
}