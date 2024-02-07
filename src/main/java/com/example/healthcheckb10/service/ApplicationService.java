package com.example.healthcheckb10.service;

import com.example.healthcheckb10.dto.application.ApplicationRequest;
import com.example.healthcheckb10.dto.application.ApplicationResponse;
import com.example.healthcheckb10.dto.application.SearchApplicationResponse;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import java.util.List;

public interface ApplicationService {
    SimpleResponse create(ApplicationRequest applicationRequest);
    SimpleResponse deleteById(List<Long> ids);
    List<ApplicationResponse> getAllApplications();
    List<SearchApplicationResponse> globalSearch(String word);
    SimpleResponse manageProceeded(Long applicationId, Boolean isProceeded);
}