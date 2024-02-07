package com.example.healthcheckb10.repositories;

import com.example.healthcheckb10.dto.appointment.response.DoctorsResponseByDepartment;
import com.example.healthcheckb10.entities.Department;
import com.example.healthcheckb10.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByImageAndFirstNameAndLastName(String image,String firstName, String lastName);
    Optional<Doctor> findDoctorByDepartmentAndId(Department department, Long doctorId);
    @Query("select new com.example.healthcheckb10.dto.appointment.response.DoctorsResponseByDepartment" +
            "(d.id,CONCAT(d.firstName,' ', d.lastName),d.image,d.position)from Doctor d join Department dep on d.department.id=dep.id where dep.id=?1 and d.isActive=true ")
    List<DoctorsResponseByDepartment> findDoctorsByDepartmentId(Long departmentId);
}