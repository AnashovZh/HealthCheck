package com.example.healthcheckb10.repositories.jdbcTemplate.jdbcTemplateImpl;

import com.example.healthcheckb10.dto.appointment.response.DoctorWithFreeDatesAndTimes;
import com.example.healthcheckb10.dto.appointment.response.DoctorWithFreeTimsheet;
import com.example.healthcheckb10.dto.doctor.DoctorResponse;
import com.example.healthcheckb10.dto.doctor.SearchDoctorResponse;
import com.example.healthcheckb10.repositories.jdbcTemplate.DoctorDao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DoctorDaoImpl implements DoctorDao {
    private final JdbcTemplate jdbcTemplate;

    private DoctorResponse rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        LocalDate endDateOfWork = resultSet.getObject("endDateOfWork", LocalDate.class);
        return new DoctorResponse(
                resultSet.getLong("id"),
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getString("image"),
                resultSet.getString("position"),
                resultSet.getBoolean("isActive"),
                resultSet.getString("departmentName"),
                endDateOfWork,
                resultSet.getString("description")
        );
    }

    @Override
    public Optional<DoctorResponse> getDoctorById(Long doctorId) {
        String sql = """
                SELECT
                d.id AS id,
                d.first_name AS firstName,
                d.last_name AS lastName,
                d.image AS image,
                d.position AS position,
                d.is_active AS isActive,
                dep.facility_name AS departmentName,
                sh.end_date_of_work AS endDateOfWork,
                d.description
                FROM doctors d 
                JOIN departments dep ON dep.id = d.department_id
                LEFT JOIN schedules sh ON d.id = sh.doctor_id
                WHERE d.id=?
                """;
        return jdbcTemplate.query(sql, this::rowMapper, doctorId)
                .stream()
                .findFirst();
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        String sql = """
                SELECT
                d.id AS id,
                d.first_name AS firstName,
                d.last_name AS lastName,
                d.image AS image,
                d.position AS position,
                d.is_active AS isActive,
                dep.facility_name AS departmentName,
                sh.end_date_of_work AS endDateOfWork,
                d.description
                FROM doctors d
                JOIN departments dep ON dep.id = d.department_id
                LEFT JOIN schedules sh ON d.id = sh.doctor_id
                ORDER BY d.id DESC
                """;
        return jdbcTemplate.query(sql, this::rowMapper);
    }

    @Override
    public List<SearchDoctorResponse>  globalSearch(String word) {
        word = "%" + word + "%";
        String sql = """
                SELECT d.id AS id,
                       d.is_active AS isActive,
                       d.image AS image,
                       d.first_name AS firstName,
                       d.last_name AS lastName,
                       d.position AS position,
                       d2.facility_name AS departmentName,
                       TO_CHAR(s.end_date_of_work, 'DD-MM-YYYY') AS scheduleUntil
                       FROM doctors d
                            JOIN departments d2 on d.department_id= d2.id
                            LEFT JOIN schedules s ON d.id = s.doctor_id
                WHERE d.first_name ILIKE ?
                   OR d.last_name ILIKE ?
                   OR d.position ILIKE ?
                   OR d2.facility_name ILIKE ?
                   OR TO_CHAR(s.end_date_of_work, 'DD/MM/YYYY') ILIKE ?
                ORDER BY d.id
                """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new SearchDoctorResponse(
                                rs.getLong("id"),
                                rs.getBoolean("isActive"),
                                rs.getString("image"),
                                rs.getString("firstName"),
                                rs.getString("lastName"),
                                rs.getString("position"),
                                rs.getString("departmentName"),
                                rs.getString("scheduleUntil")
                        ),
                word, word, word, word, word
        );
    }

    @Override
    public List<DoctorWithFreeTimsheet> findDoctorWithFreeTimesheets(Long doctorId, LocalDate dateNow) {
        String sql = """
                 SELECT
                                    d.id AS id,
                                    CONCAT(d.first_name,' ', d.last_name) AS full_name,
                                    d.image AS image,
                                    d.position AS position,
                                    t.date_of_consultation AS date_with_day_of_week,
                                    ARRAY_AGG(DISTINCT t.start_time_of_consultation) AS start_time_of_consultation
                                FROM timesheets t
                                         JOIN schedules s ON s.id = t.schedule_id
                                         JOIN schedule_day_of_week sdow ON s.id = sdow.schedule_id
                                         JOIN doctors d ON s.doctor_id = d.id
                                WHERE t.is_available = true AND d.id = ? AND sdow.day_of_week = true
                                  AND t.date_of_consultation >= CAST (? AS DATE)
                                GROUP BY t.date_of_consultation, d.id
                                limit 7;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String startTimes = rs.getString("start_time_of_consultation");
            List<String> startTimesList = Arrays.asList(startTimes.substring(1, startTimes.length() - 1).split(","));
            List<LocalTime> timesList = startTimesList.stream()
                    .map(timeStr -> LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss")))
                    .toList();
            List<String> formattedTimes = timesList.stream()
                    .map(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
                    .collect(Collectors.toList());
            return new DoctorWithFreeTimsheet(
                    rs.getLong("id"),
                    rs.getString("full_name"),
                    rs.getString("image"),
                    rs.getString("position"),
                    rs.getString("date_with_day_of_week"),
                    formattedTimes);
        }, doctorId, dateNow);
    }

    @Override
    public List<DoctorWithFreeDatesAndTimes> getDoctorWithFreeDatesAndTimes(Long doctorId, LocalDate dateNow) {
        String sql = """
                SELECT
                    d.id AS doctorId,
                    t.date_of_consultation AS date_of_consultation,
                    ARRAY_AGG(DISTINCT t.start_time_of_consultation) AS start_time_of_consultation
                FROM timesheets t
                         JOIN schedules s ON s.id = t.schedule_id
                         JOIN schedule_day_of_week sdow ON s.id = sdow.schedule_id
                         JOIN doctors d ON s.doctor_id = d.id
                WHERE t.is_available = true AND d.id = ? AND sdow.day_of_week = true
                  AND t.date_of_consultation >= CAST (? AS DATE)
                GROUP BY t.date_of_consultation, d.id;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String startTimes = rs.getString("start_time_of_consultation");
            List<String> startTimesList = Arrays.asList(startTimes.substring(1, startTimes.length() - 1).split(","));
            List<LocalTime> timesList = startTimesList.stream()
                    .map(timeStr -> LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss")))
                    .toList();
            List<String> formattedTimes = timesList.stream()
                    .map(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
                    .collect(Collectors.toList());
            return new DoctorWithFreeDatesAndTimes(
                    rs.getLong("doctorId"),
                    rs.getString("date_of_consultation"),
                    formattedTimes
            );
        }, doctorId, dateNow);
    }

    @Override
    public int deleteById(Long doctorId) {
        String sql="DELETE FROM doctors d WHERE d.id= ? AND d.is_active= FALSE";
        return jdbcTemplate.update(sql, doctorId);
    }
}