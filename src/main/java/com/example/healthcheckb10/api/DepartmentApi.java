package com.example.healthcheckb10.api;

import com.example.healthcheckb10.dto.department.DepartmentResponse;
import com.example.healthcheckb10.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Department API")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DepartmentApi {
    private final DepartmentService departmentService;

    @PermitAll
    @GetMapping("/{departmentId}")
    @Operation(summary = "Метод getDepartmentById")
    public DepartmentResponse getDepartmentId(@PathVariable Long departmentId) {
        return departmentService.getDepartmentById(departmentId);
    }

    @PermitAll
    @GetMapping("/getAll")
    @Operation(summary = "Метод getAllDepartments")
    public List<DepartmentResponse> getAllDepartments() {
        return departmentService.getAllDepartments();
    }
}