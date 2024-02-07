package com.example.healthcheckb10.repositories;

import com.example.healthcheckb10.entities.Department;
import com.example.healthcheckb10.enums.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    Optional<Department> findDepartmentByFacilityName(Facility facilityName);
}