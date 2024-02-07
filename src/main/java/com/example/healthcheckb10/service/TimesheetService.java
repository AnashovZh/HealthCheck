package com.example.healthcheckb10.service;

import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.dto.schedule.request.ScheduleDayUpdateRequest;
import com.example.healthcheckb10.dto.timesheet.TimesheetResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TimesheetService {
    SimpleResponse updateDay(Long doctorId, LocalDate scheduleDate, List<ScheduleDayUpdateRequest> timesToUpdate);
    TimesheetResponse getById(Long scheduleId, LocalDate date);
    SimpleResponse setTemplate(Long doctorId, LocalDate scheduleDate, List<ScheduleDayUpdateRequest> timesToSet);
    SimpleResponse deleteTime(Long doctorId, LocalDate scheduleDate, LocalTime time);
}