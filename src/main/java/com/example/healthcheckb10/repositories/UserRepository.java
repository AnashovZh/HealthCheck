package com.example.healthcheckb10.repositories;

import com.example.healthcheckb10.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsUserAccountByPhoneNumber(String phoneNumber);
}