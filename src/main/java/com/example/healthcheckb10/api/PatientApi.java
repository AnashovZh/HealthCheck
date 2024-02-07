package com.example.healthcheckb10.api;

import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.dto.user.request.UserRequest;
import com.example.healthcheckb10.dto.user.response.PatientGetByIdResponse;
import com.example.healthcheckb10.dto.user.response.PatientResponse;
import com.example.healthcheckb10.dto.user.response.ProfileResponse;
import com.example.healthcheckb10.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientApi {
    private final PatientService patientService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @Operation(
            summary = "Метод для выборки всех пациентов!",
            description = """
                    Также есть возможность поиска по имени и фамилии.
                    Если слово для поиска не давать вы получите список всех пациентов
                    Права на метод имеет только админ!
                    """)
    public List<PatientResponse> getAll(@RequestParam(required = false) String word) {
        return patientService.getAllPatients(word);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{patientId}")
    @Operation(
            summary = "Метод для выборки пациента по его идентификатору!",
            description = "Права на метод имеют только админ!")
    public PatientGetByIdResponse getById(@PathVariable Long patientId) {
        return patientService.getPatientById(patientId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{patientId}")
    @Operation(summary = "Удаление пациента по его ID",
            description = "Права на метод имеет только админ!")
    public SimpleResponse deletePatientById(@PathVariable Long patientId) {
        return patientService.deletePatientById(patientId);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/profile")
    @Operation(
            summary = "Функция для профиля(личного кабинета)",
            description = "Права на метод имеет пользователь со своим токеном")
    public ProfileResponse getProfile() {
        return patientService.getProfile();
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/updateProfile")
    @Operation(
            summary = "Метод для редактирования профиля пользователя",
            description = "Права на метод имеет пользователь со своим токеном"
    )
    public ProfileResponse updateProfile(
            @RequestBody @Valid UserRequest userRequest) {
        return patientService.updateProfile(userRequest);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/updatePassword")
    @Operation(
            summary = "Метод для изменения пароля пользователя",
            description = "Права на метод имеют пользователь со своим токеном"
    )
    public SimpleResponse updateUserPasswordInProfile(
        @RequestParam String oldPassword,
        @RequestParam String newPassword) {
        return patientService.updatePassword(oldPassword, newPassword);
    }
}