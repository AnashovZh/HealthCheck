package com.example.healthcheckb10.api;

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
import com.example.healthcheckb10.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
@Tag(name = "Appointment Api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AppointmentApi {
    private final AppointmentService appointmentService;

    @GetMapping("/{appointmentId}")
    @Operation(
            summary = "Метод для выборки записи по его идентификатору!",
            description = "Доступ к методу все пациенты имеют по своему токену!")
    public AppointmentResponseForGetById getAppointmentById(@PathVariable Long appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @GetMapping
    @Operation(
            summary = "Метод для выборки всех записей пациента !",
            description = "Доступ к методу все пациенты имеют по своему токену!")
    public List<AppointmentResponse> getAllAppointment() {
        return appointmentService.getAllAppointmentByUser();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/word")
    @Operation(
            summary = "Метод для поиска услуги !",
            description = "Права на метод имеют только админ." +
                    "ОБРАТИТЕ ВНИМАНИЕ,что формат для поиска по дате - 'DD/MM/YYYY'")
    public List<SearchResponse> globalSearch(@RequestParam String word) {
        return appointmentService.globalSearch(word);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/createOnlineAppointments")
    @Operation(
            summary = "Метод для создании онлайн записи !",
            description = "Доступ к методу все пациенты имеют по своему токену!"
    )
    public OnlineAppointmentResponse createOnlineAppointments(@RequestBody @Valid OnlineAppointmentRequest appointmentRequest) throws MessagingException {
        return appointmentService.createOnlineAppointment(appointmentRequest);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/received")
    @Operation(summary = "Метод для получения кода для онлайн записи.",
            description = "Доступ к методу все пациенты имеют по своему токену!")
    public SimpleResponse receiveCode(@RequestParam String email) {
        return appointmentService.receiveCode(email);
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping("/canceled")
    @Operation(summary = "Метод для oтмены записи по токену пациента.",
            description = "С помощью этого метода пациент может отменить свою  запись к врачу. Только для пациентов!")
    public SimpleResponse canceled(@RequestParam Long appointmentId) {
        return appointmentService.canceled(appointmentId);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/getDoctorsByDepartment")
    @Operation(summary = "Метод для получения докторов по департменту.",
            description = "Метод позволяет увидеть врачей одного департмента."+
                    "Доступ к методу доступен для всех, у кого есть соответствующие токены")
    public List<DoctorsResponseByDepartment> getDoctorsByDepartment(@RequestParam Long departmentId) {
        return appointmentService.getDoctorsByDepartmentId(departmentId);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/getDoctorWithFreeTimesheets")
    @Operation(summary = "Метод для получения информации о враче и его свободных времен.",
            description = "Этот метод возвращает информацию о враче, включая его основные данные, специализацию и расписание." +
                    "Свободные времена включают периоды, когда врач доступен для новых записей на прием. ")
    public List<DoctorWithFreeTimsheet> getDoctorWithFreeTimesheets(@RequestParam Long doctorId) {
        return appointmentService.getDoctorWithFreeTimesheets(doctorId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getAllOnlineAppointments")
    @Operation(summary = "Метод для выборки всех онлайн записей пациента !",
            description = "Доступ к методу имеют только админ")
    public List<SearchResponse> getAllOnlineAppointments() {
        return appointmentService.getAllOnlineAppointments();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    @Operation(summary = "Метод для удаления онлайн записи по ID!",
            description = "Доступ к методу имеют только админ")
    public SimpleResponse deleteAppointmentById(@RequestBody List<Long>appointmentIds) {
        return appointmentService.deleteAppointmentById(appointmentIds);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/getFreeDateAndTimes")
    @Operation(summary = "Метод для получения свободных времен врача для онлайн записи .",
            description = "Доступ к методу имеют пациент по своему токену!")
    public List<DoctorWithFreeDatesAndTimes> getDoctorWithFreeDatesAndTimes(@RequestParam Long doctorId) {
        return appointmentService.getDoctorWithFreeDatesAndTimes(doctorId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    @Operation(summary = "Метод для управления статусом обработки онлайн записей !",
            description = "Доступ к методу имеют только админ !")
    public SimpleResponse updateStatusAppointmentById(@RequestParam Long appointmentId,
                                                      @RequestParam Status status) {
        return appointmentService.updateStatusAppointmentById(appointmentId, status);
    }
}