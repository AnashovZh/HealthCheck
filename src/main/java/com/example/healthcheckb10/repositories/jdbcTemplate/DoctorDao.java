package com.example.healthcheckb10.repositories.jdbcTemplate;

import com.example.healthcheckb10.dto.appointment.response.DoctorWithFreeDatesAndTimes;
import com.example.healthcheckb10.dto.appointment.response.DoctorWithFreeTimsheet;
import com.example.healthcheckb10.dto.doctor.DoctorResponse;
import com.example.healthcheckb10.dto.doctor.SearchDoctorResponse;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorDao {
    Optional<DoctorResponse> getDoctorById(Long doctorId);
    List<DoctorResponse> getAllDoctors();
    List<SearchDoctorResponse>globalSearch(String word);
    List<DoctorWithFreeTimsheet>findDoctorWithFreeTimesheets(Long doctorId,LocalDate dateNow);
    List<DoctorWithFreeDatesAndTimes> getDoctorWithFreeDatesAndTimes(Long doctorId, LocalDate dateNow);
    int deleteById(Long doctorId);
}