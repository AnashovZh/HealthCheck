package com.example.healthcheckb10.service;

import com.example.healthcheckb10.dto.department.DepartmentResponse;
import java.util.List;

public interface DepartmentService {
    DepartmentResponse getDepartmentById(Long departmentId);
    List<DepartmentResponse> getAllDepartments();
}