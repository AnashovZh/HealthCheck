package com.example.healthcheckb10.api;

import com.example.healthcheckb10.dto.doctor.DoctorRequest;
import com.example.healthcheckb10.dto.doctor.DoctorResponse;
import com.example.healthcheckb10.dto.doctor.SearchDoctorResponse;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctors")
@Tag(name = "Doctor Api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DoctorApi {
    private final DoctorService doctorService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{departmentId}")
    @Operation(
            summary = "Метод для добавления врача",
            description = "Права на метод имеет только админ!")
    public SimpleResponse createDoctor(@RequestBody @Valid DoctorRequest doctorRequest,
                                       @PathVariable Long departmentId) {
        return doctorService.createDoctor(doctorRequest, departmentId);
    }

    @PermitAll
    @GetMapping("/{doctorId}")
    @Operation(
            summary = "Метод для нахождения доктора по его идентификатору",
            description = "Права на метод имеют все!")
    public DoctorResponse getDoctorById(@PathVariable Long doctorId) {
        return doctorService.getDoctorById(doctorId);
    }

    @PermitAll
    @GetMapping
    @Operation(
            summary = "Метод для выборки всех докторов",
            description = "Права на метод имеют все!")
    public List<DoctorResponse> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update")
    @Operation(
            summary = "Метод для обновления информации доктора по его идентификатору",
            description = "Права на метод имеет админ!")
    public SimpleResponse updateDoctor(@RequestBody @Valid DoctorRequest doctorRequest,
                                       @RequestParam Long doctorId,
                                       @RequestParam Long departmentId) {
        return doctorService.updateDoctor(doctorRequest, doctorId, departmentId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{doctorId}")
    @Operation(
            summary = "Метод для удаления доктора по его идентификатору",
            description = "Права на метод имеют только админ")
    public SimpleResponse deleteDoctor(@PathVariable Long doctorId) {
        return doctorService.deleteDoctor(doctorId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/word")
    @Operation(
            summary = "Метод для поиска доктора!",
            description = "Права на метод имеют только админ"
    )
    public List<SearchDoctorResponse> globalSearch(String word) {
        return doctorService.globalSearch(word);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/status")
    @Operation(summary = "Метод для управления статусом доктора!",
               description = "Метод позволяет изменить статус доктора(able,disable)")
    public SimpleResponse manageStatus(@RequestParam Long doctorId,
                                       @RequestParam Boolean isActive){
        return doctorService.manageStatus(doctorId,isActive);
    }
}