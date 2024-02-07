package com.example.healthcheckb10.repositories.jdbcTemplate;

import com.example.healthcheckb10.dto.schedule.responce.ScheduleResponse;
import java.util.List;

public interface ScheduleDao {
    List<ScheduleResponse> getAll();
    List<ScheduleResponse>filterDate(String dateFrom,String dateUntil);
    List<ScheduleResponse> globalSearch(String word);
}