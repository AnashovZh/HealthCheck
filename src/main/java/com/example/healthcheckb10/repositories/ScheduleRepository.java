package com.example.healthcheckb10.repositories;

import com.example.healthcheckb10.entities.Doctor;
import com.example.healthcheckb10.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByDoctor(Doctor doctor);
    Optional<Schedule> findByDoctor(Doctor doctor);
}