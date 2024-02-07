package com.example.healthcheckb10.dto.schedule.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDayUpdateRequest {
    @NotBlank(message = "Пожалуйста убедитесь, что вы задали дату начала!")
    private String newStartTime;
    @NotBlank(message = "Пожалуйста убедитесь, что вы задали дату окончания!")
    private String newEndTime;
}