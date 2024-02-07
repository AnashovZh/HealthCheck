package com.example.healthcheckb10.repositories.jdbcTemplate;

import com.example.healthcheckb10.dto.appointment.AppointmentResponse;
import com.example.healthcheckb10.dto.appointment.AppointmentResponseForGetById;
import com.example.healthcheckb10.dto.appointment.SearchResponse;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentDao {
    Optional<AppointmentResponseForGetById> getAppointmentById(Long appointmentId);
    List<AppointmentResponse> getAllAppointmentByUserId(Long userId);
    List<SearchResponse> globalSearch(String word);
    List<SearchResponse> getAllOnlineAppointments(LocalDate dateNow);
    SimpleResponse deleteAppointmentById(Long appointmentId);
}