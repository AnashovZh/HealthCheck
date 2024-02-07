package com.example.healthcheckb10.repositories.jdbcTemplate.jdbcTemplateImpl;

import com.example.healthcheckb10.dto.timesheet.TimesheetResponse;
import com.example.healthcheckb10.repositories.jdbcTemplate.TimesheetDao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TimesheetDaoImpl implements TimesheetDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<TimesheetResponse> getById(Long scheduleId, LocalDate date) {
        String sql = """
                SELECT
                d.position,
                CONCAT(d.first_name,' ',d.last_name) AS fullName,
                t.date_of_consultation AS date,
                ARRAY_AGG(concat(t.start_time_of_consultation,' ',t.end_time_of_consultation)) AS times
                FROM timesheets t
                    JOIN schedules s ON t.schedule_id = s.id
                    JOIN doctors d ON s.doctor_id = d.id
                WHERE s.id = ? AND t.date_of_consultation = ?
                GROUP BY s.id,t.date_of_consultation, concat(d.first_name,' ',d.last_name), d.position
                                
                """;
        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) ->
                        new TimesheetResponse(
                                resultSet.getString("position"),
                                resultSet.getString("fullName"),
                                resultSet.getString("date"),
                                Collections.singletonList(resultSet.getString("times")))
                        , scheduleId,date)
                .stream()
                .findFirst();
    }
}