package com.example.healthcheckb10.dto.result;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record ResultRequest(
        @NotNull(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        Long departmentId,
        @NotNull(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        LocalDate dueDate,
        @NotNull(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        Long patientId,
        @NotNull(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String pdgFileCheque) {
}