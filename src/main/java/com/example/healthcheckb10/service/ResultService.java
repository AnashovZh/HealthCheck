package com.example.healthcheckb10.service;

import com.example.healthcheckb10.dto.result.AdminGetResultResponse;
import com.example.healthcheckb10.dto.result.GetResultResponse;
import com.example.healthcheckb10.dto.result.ResultRequest;
import com.example.healthcheckb10.dto.result.ResultResponse;
import java.util.List;

public interface ResultService {
    ResultResponse addResult(ResultRequest resultRequest);
    GetResultResponse getResult(String resultNumber);
    List<AdminGetResultResponse> getResultForAdmin(Long userId);
}