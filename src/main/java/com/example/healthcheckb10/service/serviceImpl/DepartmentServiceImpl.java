package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.dto.department.DepartmentResponse;
import com.example.healthcheckb10.entities.Department;
import com.example.healthcheckb10.exceptions.NotFoundException;
import com.example.healthcheckb10.repositories.DepartmentRepository;
import com.example.healthcheckb10.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    public DepartmentResponse getDepartmentById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> {
            log.info("Отделение под номером %s не найден!".formatted(departmentId));
            return new NotFoundException("Отделение под номером %s не найден!".formatted(departmentId));
        });
        DepartmentResponse response=new DepartmentResponse();
        response.setId(department.getId());
        response.setFacilityName(department.getFacilityName().getRussianName());
        return response;
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        List<Department> departmentRepositoryAll = departmentRepository.findAll();
        List<DepartmentResponse> departments = new ArrayList<>();
        for (Department department : departmentRepositoryAll) {
            DepartmentResponse response=new DepartmentResponse();
            response.setId(department.getId());
            response.setFacilityName(department.getFacilityName().getRussianName());
            departments.add(response);
        }
        return departments;
    }
}