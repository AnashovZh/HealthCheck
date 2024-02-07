package com.example.healthcheckb10.api;

import com.example.healthcheckb10.dto.application.ApplicationRequest;
import com.example.healthcheckb10.dto.application.ApplicationResponse;
import com.example.healthcheckb10.dto.application.SearchApplicationResponse;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Application Api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApplicationApi {
    private final ApplicationService service;

    @PostMapping("/add")
    @Operation(
            summary = "Метод для заявки!",
            description = "Метод позволяет оставить заявку на прием,оставив номер телефона! " +
                    "Метод доступен всем!")
    public SimpleResponse create(@RequestBody @Valid ApplicationRequest request) {
        return service.create(request);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    @Operation(
            summary = "Удаление заявки по ее ID!",
            description = "Метод позволяет удалить оставленную заявку по ее ID." +
                    "Права на метод имеет только админ!")
    public SimpleResponse delete(@RequestBody List<Long>ids) {
        return service.deleteById(ids);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getAll")
    @Operation(
            summary = "Поиск всех заявок!",
            description = "Метод позволяет найти все оставленные заявки" +
                    "Права на метод имеет только админ!")
    public List<ApplicationResponse> getAllApplications() {
        return service.getAllApplications();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/word")
    @Operation(
            summary = "Метод для поиска заявок!",
            description = "Права на метод имеют только админ"
    )
    public List<SearchApplicationResponse> globalSearch(String word) {
        return service.globalSearch(word);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Метод для управления статусом обработки заявок!",
            description = "Права на метод имеют только админ"
    )
    public SimpleResponse manageProceeded(@RequestParam Long applicationId,
                                          @RequestParam Boolean isProceeded){
        return service.manageProceeded(applicationId,isProceeded);
    }
}
