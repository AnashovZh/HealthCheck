package com.example.healthcheckb10.service;

import com.example.healthcheckb10.dto.appointment.AppointmentResponse;
import com.example.healthcheckb10.dto.appointment.AppointmentResponseForGetById;
import com.example.healthcheckb10.dto.appointment.OnlineAppointmentResponse;
import com.example.healthcheckb10.dto.appointment.SearchResponse;
import com.example.healthcheckb10.dto.appointment.request.OnlineAppointmentRequest;
import com.example.healthcheckb10.dto.appointment.response.DoctorWithFreeDatesAndTimes;
import com.example.healthcheckb10.dto.appointment.response.DoctorWithFreeTimsheet;
import com.example.healthcheckb10.dto.appointment.response.DoctorsResponseByDepartment;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.enums.Status;
import com.example.healthcheckb10.validation.MyEmailValidation;
import jakarta.mail.MessagingException;
import java.util.List;

public interface AppointmentService {
    AppointmentResponseForGetById getAppointmentById(Long appointmentId);
    List<AppointmentResponse> getAllAppointmentByUser();
    List<SearchResponse> globalSearch(String word);
    OnlineAppointmentResponse createOnlineAppointment(OnlineAppointmentRequest appointmentRequest) throws MessagingException;
    SimpleResponse receiveCode(@MyEmailValidation String email);
    SimpleResponse canceled(Long appointmentId);
    List<DoctorsResponseByDepartment> getDoctorsByDepartmentId(Long departmentId);
    List<DoctorWithFreeTimsheet> getDoctorWithFreeTimesheets(Long doctorId);
    List<SearchResponse> getAllOnlineAppointments();
    SimpleResponse deleteAppointmentById(List<Long> appointmentIds);
    SimpleResponse updateStatusAppointmentById(Long appointmentId, Status status);
    List<DoctorWithFreeDatesAndTimes> getDoctorWithFreeDatesAndTimes(Long doctorId);
}