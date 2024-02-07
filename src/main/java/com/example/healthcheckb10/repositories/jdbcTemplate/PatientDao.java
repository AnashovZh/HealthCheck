package com.example.healthcheckb10.repositories.jdbcTemplate;

import com.example.healthcheckb10.dto.user.response.PatientGetByIdResponse;
import com.example.healthcheckb10.dto.user.response.PatientResponse;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientDao {
    Optional<PatientGetByIdResponse>getPatientById(Long id);
    List<PatientResponse> getAllPatients();
    List<PatientResponse> getAllPatientsBySearch(String word);
}