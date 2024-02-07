package com.example.healthcheckb10.dto.user.request;

import com.example.healthcheckb10.validation.MyEmailValidation;
import com.example.healthcheckb10.validation.PhoneNumberValidation;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(@NotBlank(message = "Пожалуйста убедитесь, что вы заполнили  поле для имени !")
                          String firstName,
                          @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили поле для фамилии !")
                          String lastName,
                          @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили поле для почты !")
                          @MyEmailValidation
                          String email,
                          @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили поле для номера телефона !")
                          @PhoneNumberValidation
                          String phoneNumber) {
}
