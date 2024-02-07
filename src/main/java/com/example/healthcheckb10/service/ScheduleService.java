package com.example.healthcheckb10.service;

import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.dto.schedule.request.CreateDoctorScheduleRequest;
import com.example.healthcheckb10.dto.schedule.responce.ScheduleResponse;
import java.util.List;

public interface ScheduleService {
    SimpleResponse createSchedule(CreateDoctorScheduleRequest createDoctorScheduleRequest);
    List<ScheduleResponse> getAll(String dateFrom,String dateUntil);
    List<ScheduleResponse> globalSearch(String word);
}