package com.example.healthcheckb10.repositories.jdbcTemplate.jdbcTemplateImpl;

import com.example.healthcheckb10.dto.appointment.AppointmentResponse;
import com.example.healthcheckb10.dto.appointment.AppointmentResponseForGetById;
import com.example.healthcheckb10.dto.appointment.SearchResponse;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.repositories.jdbcTemplate.AppointmentDao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AppointmentDaoImpl implements AppointmentDao {
    private final JdbcTemplate jdbcTemplate;

    private AppointmentResponseForGetById rowMapperForGetById(ResultSet resultSet, int rowNum) throws SQLException {
        return new AppointmentResponseForGetById(
                resultSet.getLong("id"),
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getString("email"),
                resultSet.getString("phoneNumber"),
                resultSet.getDate("localDate").toLocalDate(),
                resultSet.getString("time"),
                resultSet.getString("doctorFullName"),
                resultSet.getString("departmentName"),
                resultSet.getString("status"));
    }

    private AppointmentResponse rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new AppointmentResponse(
                resultSet.getLong("id"),
                resultSet.getDate("localDate").toLocalDate(),
                resultSet.getString("time"),
                resultSet.getString("doctorFullName"),
                resultSet.getString("status"),
                resultSet.getString("position"));
    }

    @Override
    public Optional<AppointmentResponseForGetById> getAppointmentById(Long appointmentId) {
        String sql = """
                SELECT
                    a.id AS id,
                    u.first_name AS firstName,
                    u.last_name AS lastName,
                    ua.email AS email,
                    u.phone_number AS phoneNumber,
                    DATE(a.date_of_visiting) AS localDate,
                    TO_CHAR(a.time_of_visiting, 'HH24:MI') AS time,
                    concat(d.first_name,' ',d.last_name) AS doctorFullName,
                    dep.facility_name AS departmentName,
                    a.status AS status
                FROM appointments a
                         JOIN users u ON a.user_id = u.id
                         JOIN user_accounts ua ON ua.user_id = u.id
                         JOIN doctors d ON d.id = a.doctor_id
                         JOIN departments dep ON a.department_id = dep.id WHERE a.id = ?;
                     """;
        return jdbcTemplate.query(sql, this::rowMapperForGetById, appointmentId)
                .stream()
                .findFirst();
    }

    @Override
    public List<AppointmentResponse> getAllAppointmentByUserId(Long userId) {
        String sql = """
                SELECT
                    a.id AS id,
                    DATE(a.date_of_visiting) AS localDate,
                    TO_CHAR(a.time_of_visiting, 'HH24:MI') AS time,
                    concat(d.first_name,' ',d.last_name) AS doctorFullName,
                    a.status AS status,
                    d.position AS position
                FROM appointments a
                         JOIN users u ON a.user_id = u.id
                         JOIN doctors d ON d.id = a.doctor_id WHERE a.user_id = ?;
                     """;
        return jdbcTemplate.query(sql, this::rowMapper, userId);
    }

    @Override
    public List<SearchResponse> globalSearch(String word) {
        word = "%" + word + "%";
        String sql = """
            SELECT a.id AS id,
                   CONCAT(u.first_name, ' ', u.last_name) AS patientFullName,
                   u.phone_number AS phoneNumber,
                   ua.email AS email,
                   d.position AS position,
                   CONCAT(d.first_name, ' ', d.last_name) AS doctorFullName,
                   CONCAT(a.date_of_visiting, ' ', TO_CHAR(a.time_of_visiting, 'HH24:MI')) AS dateAndTime,
                   a.status AS status
            FROM appointments a
                     JOIN users u ON a.user_id = u.id
                     JOIN user_accounts ua ON u.id = ua.user_id
                     JOIN doctors d ON d.id = a.doctor_id
                     JOIN departments d2 ON d2.id = d.department_id
            WHERE u.first_name ILIKE ?
               OR u.last_name ILIKE ?
               OR ua.email ILIKE ?
               OR d.position ILIKE ?
               OR u.phone_number ILIKE ?
               OR TO_CHAR(a.date_of_visiting, 'DD/MM/YYYY') ILIKE ?
               OR d.first_name ILIKE ?
               OR d.last_name ILIKE ?
            
            """;
        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) ->
                        new SearchResponse(
                                resultSet.getLong("id"),
                                resultSet.getString("patientFullName"),
                                resultSet.getString("phoneNumber"),
                                resultSet.getString("email"),
                                resultSet.getString("position"),
                                resultSet.getString("doctorFullName"),
                                resultSet.getString("dateAndTime"),
                                resultSet.getString("status")
                        ),
                word, word, word, word, word, word, word,word
        );
    }

    @Override
    public List<SearchResponse> getAllOnlineAppointments(LocalDate dateNow) {
        String sql = """
            SELECT 
                a.id,
                CONCAT(u.first_name, ' ', u.last_name) AS full_name,
                u.phone_number,
                ua.email,
                d.position,
                CONCAT(d.first_name, ' ', d.last_name) AS doctorFullName,
                CONCAT(a.date_of_visiting, ' ', TO_CHAR(a.time_of_visiting, 'HH24:MI')) AS time_of_visiting,
                a.status
            FROM 
                appointments a
                JOIN doctors d ON d.id = a.doctor_id
                JOIN users u ON a.user_id = u.id
                JOIN user_accounts ua ON u.id = ua.user_id
            WHERE 
                (a.status = 'CONFIRMED' OR a.status = 'COMPLETED')
                AND a.date_of_visiting >= CAST(? AS DATE)
            GROUP BY 
                a.id, full_name, u.phone_number, ua.email, d.position, doctorFullName, a.date_of_visiting, time_of_visiting, a.status
            ORDER BY a.id DESC 
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SearchResponse searchResponse=new SearchResponse();
            searchResponse.setId(rs.getLong("id"));
            searchResponse.setPatientFullName(rs.getString("full_name"));
            searchResponse.setPhoneNumber(rs.getString("phone_number"));
            searchResponse.setEmail(rs.getString("email"));
            searchResponse.setPosition(rs.getString("position"));
            searchResponse.setDoctorFullName(rs.getString("doctorFullName"));
            searchResponse.setDateAndTime(rs.getString("time_of_visiting"));
            searchResponse.setStatus(rs.getString("status"));
            return searchResponse;
        }, dateNow);
    }

    @Override
    public SimpleResponse deleteAppointmentById(Long appointmentId) {
        String sql= """
                delete from appointments a where a.id= ? and a.status='COMPLETED'
                """;
        jdbcTemplate.update(sql, appointmentId);
        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message("Онлайн запись успешно удалена !")
                .build();
    }
}