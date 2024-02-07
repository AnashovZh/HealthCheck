package com.example.healthcheckb10.dto.application;

import com.example.healthcheckb10.validation.PhoneNumberValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ApplicationRequest(
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String name,
        @NotNull(message = "Поле не должно быть пустым!")
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        @PhoneNumberValidation(message = "Неверный формат телефона!")
        String phoneNumber) {
}