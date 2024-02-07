package com.example.healthcheckb10.repositories;

import com.example.healthcheckb10.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application,Long> {
    boolean existsByPhoneNumber(String phoneNumber);
}