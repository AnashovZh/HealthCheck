package com.example.healthcheckb10.service;

import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.dto.user.response.PatientGetByIdResponse;
import com.example.healthcheckb10.dto.user.response.PatientResponse;
import com.example.healthcheckb10.dto.user.request.UserRequest;
import com.example.healthcheckb10.dto.user.response.ProfileResponse;
import com.example.healthcheckb10.validation.PasswordValidation;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public interface PatientService {
    PatientGetByIdResponse getPatientById(Long id);
    List<PatientResponse> getAllPatients(String word);
    SimpleResponse deletePatientById(Long id);
    ProfileResponse getProfile();
    ProfileResponse updateProfile(UserRequest userRequest);
    SimpleResponse updatePassword(@NotBlank String oldPassword, @PasswordValidation String newPassword);
}