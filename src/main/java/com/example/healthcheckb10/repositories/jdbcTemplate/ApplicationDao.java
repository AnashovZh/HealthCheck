package com.example.healthcheckb10.repositories.jdbcTemplate;

import com.example.healthcheckb10.dto.application.ApplicationResponse;
import com.example.healthcheckb10.dto.application.SearchApplicationResponse;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationDao {
    Optional<ApplicationResponse> getById(Long id);
    List<ApplicationResponse> getAllApplications();
    List<SearchApplicationResponse>globalSearch(String word);
}