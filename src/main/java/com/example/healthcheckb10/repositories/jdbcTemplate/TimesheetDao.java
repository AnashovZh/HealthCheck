package com.example.healthcheckb10.repositories.jdbcTemplate;

import com.example.healthcheckb10.dto.timesheet.TimesheetResponse;
import java.time.LocalDate;
import java.util.Optional;

public interface TimesheetDao {
    Optional<TimesheetResponse> getById(Long scheduleId, LocalDate date);
}