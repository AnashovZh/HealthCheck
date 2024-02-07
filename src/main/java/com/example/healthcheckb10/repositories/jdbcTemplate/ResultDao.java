package com.example.healthcheckb10.repositories.jdbcTemplate;

import com.example.healthcheckb10.dto.result.AdminGetResultResponse;
import java.util.List;

public interface ResultDao {
    List<AdminGetResultResponse> getResultForAdmin(Long userId);
}
