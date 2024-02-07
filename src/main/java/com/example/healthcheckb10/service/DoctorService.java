package com.example.healthcheckb10.service;

import com.example.healthcheckb10.dto.doctor.DoctorRequest;
import com.example.healthcheckb10.dto.doctor.DoctorResponse;
import com.example.healthcheckb10.dto.doctor.SearchDoctorResponse;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import java.util.List;

public interface DoctorService {
    SimpleResponse createDoctor(DoctorRequest doctorRequest, Long departmentId);
    DoctorResponse getDoctorById(Long doctorId);
    List<DoctorResponse> getAllDoctors();
    SimpleResponse updateDoctor(DoctorRequest doctorRequest, Long doctorId, Long departmentId);
    SimpleResponse deleteDoctor(Long doctorId);
    List<SearchDoctorResponse> globalSearch(String word);
    SimpleResponse manageStatus(Long doctorId, Boolean isActive);
}