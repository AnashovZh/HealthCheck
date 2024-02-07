package com.example.healthcheckb10.repositories;

import com.example.healthcheckb10.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result,Long> {
    Optional<Result> findByResultNumber(String resultNumber);
}