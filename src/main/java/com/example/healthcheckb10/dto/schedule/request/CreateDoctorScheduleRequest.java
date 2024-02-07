package com.example.healthcheckb10.dto.schedule.request;
import com.example.healthcheckb10.enums.Day;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Map;

public record CreateDoctorScheduleRequest(
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String departmentName,
        @NotNull(message = "Поле не может быть пустым!")
        Long doctorId,
        @FutureOrPresent(message = "Вы не можете указать уже прошедшую дату в качестве даты начала!")
        @NotNull(message = "Поле не может быть пустым!")
        LocalDate startDateOfWork,
        @FutureOrPresent(message = "Вы не можете указать уже прошедшую дату в качестве даты окончания!")
        @NotNull(message = "Поле не может быть пустым!")
        LocalDate endDateOfWork,
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String startTimeOfWork,
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String endTimeOfWork,
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String startBreakTime,
        @NotBlank(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        String endBreakTime,
        @Min(value = 30, message = "Интервал времени Вы можете задать только 30, 45, 60 или 90 минут!")
        @Max(value = 90, message = "Интервал времени Вы можете задать только 30, 45, 60 или 90 минут!")
        int intervalInMinutes,
        @NotNull(message = "Пожалуйста убедитесь, что вы заполнили все поля!")
        Map<Day, Boolean> dayOfWeek) {
}