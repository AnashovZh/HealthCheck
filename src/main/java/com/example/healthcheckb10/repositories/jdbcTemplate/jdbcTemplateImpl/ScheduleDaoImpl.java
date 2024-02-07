package com.example.healthcheckb10.repositories.jdbcTemplate.jdbcTemplateImpl;

import com.example.healthcheckb10.dto.schedule.responce.DateInfo;
import com.example.healthcheckb10.dto.schedule.responce.ScheduleResponse;
import com.example.healthcheckb10.repositories.jdbcTemplate.ScheduleDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class ScheduleDaoImpl implements ScheduleDao {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<ScheduleResponse> getAll() {
        String sql = """
                    SELECT
                        scheduleId,
                        doctorId,
                        doctorImage,
                        doctorFullName,
                        doctorPosition,
                        json_agg(
                            json_build_object(
                                'dateDay', dateDay,
                                'dayOfWeek', dayOfWeek,
                                'timeIntervals', timeIntervals
                            ) ORDER BY dateDay
                        ) AS dateDayTimeInfos
                    FROM (
                        SELECT
                            t.schedule_id AS scheduleId,
                            d.id AS doctorId,
                            d.image AS doctorImage,
                            CONCAT(d.first_name, ' ', d.last_name) AS doctorFullName,
                            d.position AS doctorPosition,
                            t.date_of_consultation AS dateDay,
                            CASE
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 1 THEN 'MONDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 2 THEN 'TUESDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 3 THEN 'WEDNESDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 4 THEN 'THURSDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 5 THEN 'FRIDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 6 THEN 'SATURDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 7 THEN 'SUNDAY'
                            END AS dayOfWeek,
                            json_agg(
                                json_build_object(
                                    'startTime', t.start_time_of_consultation,
                                    'endTime', t.end_time_of_consultation,
                                    'isAvailable',t.is_available
                                ) ORDER BY t.start_time_of_consultation
                            ) AS timeIntervals
                        FROM timesheets t
                        JOIN schedules s ON t.schedule_id = s.id
                        JOIN doctors d ON s.doctor_id = d.id
                        WHERE t.schedule_id IN (SELECT schedule_id FROM schedule_day_of_week)
                                                  AND t.date_of_consultation>=CURRENT_DATE
                        GROUP BY t.schedule_id, t.date_of_consultation, d.id,d.image, d.first_name, d.last_name, d.position
                        ORDER BY t.date_of_consultation
                    ) AS subquery
                    GROUP BY scheduleId, doctorId,doctorImage, doctorFullName, doctorPosition
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            long scheduleId = rs.getLong("scheduleId");
            long doctorId = rs.getLong("doctorId");
            String doctorImage = rs.getString("doctorImage");
            String doctorFullName = rs.getString("doctorFullName");
            String doctorPosition = rs.getString("doctorPosition");
            ObjectMapper objectMapper = new ObjectMapper();
            List<DateInfo> scheduleDayTimeInfoList;
            try {
                scheduleDayTimeInfoList = objectMapper.readValue(
                        rs.getString("dateDayTimeInfos"),
                        new TypeReference<>() {
                        }
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (scheduleDayTimeInfoList == null || scheduleDayTimeInfoList.isEmpty()) {
                scheduleDayTimeInfoList = Collections.emptyList();
            } else {
                Map<String, DateInfo> dayTimeMap = new HashMap<>();
                for (DateInfo dateInfo : scheduleDayTimeInfoList) {
                    dayTimeMap.put(dateInfo.getDayOfWeek(), dateInfo);
                }
                LocalDate currentDate = LocalDate.now();
                List<LocalDate> allDates = new ArrayList<>();
                for (int i = 0; i < 14; i++) {
                    allDates.add(currentDate.plusDays(i));
                }
                for (LocalDate date : allDates) {
                    String dayOfWeek = date.getDayOfWeek().toString();
                    if (!dayTimeMap.containsKey(dayOfWeek)) {
                        DateInfo emptyDateInfo = new DateInfo(date.toString(), dayOfWeek, Collections.emptyList());
                        scheduleDayTimeInfoList.add(emptyDateInfo);
                    }
                }
                scheduleDayTimeInfoList.sort(Comparator.comparing(DateInfo::getDateDay));
            }

            return new ScheduleResponse(
                    scheduleId,
                    doctorId,
                    doctorImage,
                    doctorFullName,
                    doctorPosition,
                    scheduleDayTimeInfoList
            );
        });
    }

    @Override
    public List<ScheduleResponse> filterDate(String dateFrom, String dateUntil) {
        LocalDate fromDate = LocalDate.parse(dateFrom);
        LocalDate untilDate = LocalDate.parse(dateUntil);
        String sql1 = """
        SELECT
            scheduleId,
            doctorId,
            doctorImage,
            doctorFullName,
            doctorPosition,
            json_agg(
                json_build_object(
                    'dateDay', dateDay,
                    'dayOfWeek', dayOfWeek,
                    'timeIntervals', timeIntervals
                ) ORDER BY dateDay
            ) AS dateDayTimeInfos
        FROM (
            SELECT
                t.schedule_id AS scheduleId,
                d.id AS doctorId,
                d.image AS doctorImage,
                CONCAT(d.first_name, ' ', d.last_name) AS doctorFullName,
                d.position AS doctorPosition,
                t.date_of_consultation AS dateDay,
                CASE
                    WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 1 THEN 'MONDAY'
                    WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 2 THEN 'TUESDAY'
                    WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 3 THEN 'WEDNESDAY'
                    WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 4 THEN 'THURSDAY'
                    WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 5 THEN 'FRIDAY'
                    WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 6 THEN 'SATURDAY'
                    WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 7 THEN 'SUNDAY'
                END AS dayOfWeek,
                json_agg(
                    json_build_object(
                        'startTime', t.start_time_of_consultation,
                        'endTime', t.end_time_of_consultation,
                        'isAvailable',t.is_available
                    ) ORDER BY t.start_time_of_consultation
                ) AS timeIntervals
            FROM timesheets t
            JOIN schedules s ON t.schedule_id = s.id
            JOIN doctors d ON s.doctor_id = d.id
            WHERE t.schedule_id IN (SELECT schedule_id FROM schedule_day_of_week)
                AND t.date_of_consultation >= CAST(? AS DATE)
                AND t.date_of_consultation <= CAST(? AS DATE)
            GROUP BY t.schedule_id, t.date_of_consultation,d.id, d.image, d.first_name, d.last_name, d.position
            ORDER BY t.date_of_consultation
        ) AS subquery
        WHERE subquery.dateDay BETWEEN ? AND ?
        GROUP BY scheduleId,doctorId, doctorImage, doctorFullName, doctorPosition
    """;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", new Locale.Builder().setLanguage("en").build());
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql1, fromDate, untilDate, fromDate, untilDate);
        return results.stream().map(result -> {
            long scheduleId = Long.parseLong(String.valueOf(result.get("scheduleId")));
            long doctorId = Long.parseLong(String.valueOf(result.get("doctorId")));
            String doctorImage = (String) result.get("doctorImage");
            String doctorFullName = (String) result.get("doctorFullName");
            String doctorPosition = (String) result.get("doctorPosition");
            ObjectMapper objectMapper = new ObjectMapper();
            List<DateInfo> scheduleDayTimeInfoList;
            try {
                scheduleDayTimeInfoList = objectMapper.readValue(
                        String.valueOf(result.get("dateDayTimeInfos")),
                        new TypeReference<>() {
                        }
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            List<LocalDate> allDates = Stream.iterate(fromDate, date -> date.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(fromDate, untilDate.plusDays(1)))
                    .toList();
            List<DateInfo> finalScheduleDayTimeInfoList = scheduleDayTimeInfoList;
            scheduleDayTimeInfoList = allDates.stream()
                    .map(date -> finalScheduleDayTimeInfoList.stream()
                            .filter(info -> LocalDate.parse(info.getDateDay()).isEqual(date))
                            .findFirst()
                            .orElse(new DateInfo(date.format(dateFormatter), date.format(dayOfWeekFormatter), new ArrayList<>())))
                    .peek(existingDateInfo -> existingDateInfo.setTimeIntervals(existingDateInfo.getTimeIntervals() != null ? existingDateInfo.getTimeIntervals() : new ArrayList<>()))
                    .collect(Collectors.toList());
            return new ScheduleResponse(scheduleId,doctorId, doctorImage, doctorFullName, doctorPosition, scheduleDayTimeInfoList);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> globalSearch(String word) {
        String sql = """
                    SELECT
                        scheduleId,
                        doctorId,
                        doctorImage,
                        doctorFullName,
                        doctorPosition,
                        json_agg(
                            json_build_object(
                                'dateDay', dateDay,
                                'dayOfWeek', dayOfWeek,
                                'timeIntervals', timeIntervals
                            ) ORDER BY dateDay
                        ) AS dateDayTimeInfos
                    FROM (
                        SELECT
                            t.schedule_id AS scheduleId,
                            d.id AS doctorId,
                            d.image AS doctorImage,
                            CONCAT(d.first_name, ' ', d.last_name) AS doctorFullName,
                            d.position AS doctorPosition,
                            t.date_of_consultation AS dateDay,
                            CASE
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 1 THEN 'MONDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 2 THEN 'TUESDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 3 THEN 'WEDNESDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 4 THEN 'THURSDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 5 THEN 'FRIDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 6 THEN 'SATURDAY'
                                WHEN EXTRACT(ISODOW FROM t.date_of_consultation) = 7 THEN 'SUNDAY'
                            END AS dayOfWeek,
                            json_agg(
                                json_build_object(
                                    'startTime', t.start_time_of_consultation,
                                    'endTime', t.end_time_of_consultation,
                                    'isAvailable',t.is_available
                                ) ORDER BY t.start_time_of_consultation
                            ) AS timeIntervals
                        FROM timesheets t
                        JOIN schedules s ON t.schedule_id = s.id
                        JOIN doctors d ON s.doctor_id = d.id
                        WHERE t.schedule_id IN (SELECT schedule_id FROM schedule_day_of_week)
                        AND d.first_name ILIKE '%s' OR d.last_name ILIKE '%s' OR d.position ILIKE '%s'
                        GROUP BY t.schedule_id, t.date_of_consultation,d.id, d.image, d.first_name, d.last_name, d.position
                        ORDER BY t.date_of_consultation
                    ) AS subquery
                   
                    GROUP BY scheduleId,doctorId, doctorImage, doctorFullName, doctorPosition
                """;
        word = "%" + word + "%";
        sql = String.format(sql, word, word, word);
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            long scheduleId = rs.getLong("scheduleId");
            long doctorId = rs.getLong("doctorId");
            String doctorImage = rs.getString("doctorImage");
            String doctorFullName = rs.getString("doctorFullName");
            String doctorPosition = rs.getString("doctorPosition");
            ObjectMapper objectMapper = new ObjectMapper();
            List<DateInfo> scheduleDayTimeInfoList;
            try {
                scheduleDayTimeInfoList = objectMapper.readValue(
                        rs.getString("dateDayTimeInfos"),
                        new TypeReference<>() {
                        }
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (scheduleDayTimeInfoList == null || scheduleDayTimeInfoList.isEmpty()) {
                scheduleDayTimeInfoList = Collections.emptyList();
            } else {
                Map<String, DateInfo> dayTimeMap = new HashMap<>();
                for (DateInfo dateInfo : scheduleDayTimeInfoList) {
                    dayTimeMap.put(dateInfo.getDayOfWeek(), dateInfo);
                }
                LocalDate currentDate = LocalDate.now();
                List<LocalDate> allDates = new ArrayList<>();
                for (int i = 0; i < 14; i++) {
                    allDates.add(currentDate.plusDays(i));
                }
                for (LocalDate date : allDates) {
                    String dayOfWeek = date.getDayOfWeek().toString();
                    if (!dayTimeMap.containsKey(dayOfWeek)) {
                        DateInfo emptyDateInfo = new DateInfo(date.toString(), dayOfWeek, Collections.emptyList());
                        scheduleDayTimeInfoList.add(emptyDateInfo);
                    }
                }
                scheduleDayTimeInfoList.sort(Comparator.comparing(DateInfo::getDateDay));
            }
            return new ScheduleResponse(scheduleId,doctorId, doctorImage, doctorFullName, doctorPosition, scheduleDayTimeInfoList);
        });
    }
}