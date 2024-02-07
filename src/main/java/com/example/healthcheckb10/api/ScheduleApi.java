package com.example.healthcheckb10.api;

import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.dto.schedule.request.CreateDoctorScheduleRequest;
import com.example.healthcheckb10.dto.schedule.responce.ScheduleResponse;
import com.example.healthcheckb10.service.ScheduleService;
import com.example.healthcheckb10.service.serviceImpl.ExportToExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule Api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ScheduleApi {
    private final ScheduleService scheduleService;
    private final ExportToExcelService exportToExcelService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(summary = "Метод для создания расписания специалиста!",
            description = """
                    Для создания расписания, где указываются отделение, доктор,
                    график работы, интервал времени, перерыв и дни повторения.
                    Права на метод имеет только Админ!
                    """)
    public SimpleResponse createSchedule(@RequestBody @Valid CreateDoctorScheduleRequest request) {
        return scheduleService.createSchedule(request);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @Operation(summary = "Метод для получения расписания всех врачей!" +
            " И для фильтрации по дате!",
            description = """
                    Метод позволяет увидеть расписание врачей
                    по дням и разделенным окошкам для записи
                    """)
    public List<ScheduleResponse> getAllSchedules(@RequestParam(required = false) String dateFrom,
                                                  @RequestParam(required = false) String dateUntil) {
        return scheduleService.getAll(dateFrom, dateUntil);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/export")
    @Operation(summary = "Экспорт расписания в электронную таблицу Excel",
            description = "Экспортирует расписание в электронную таблицу Excel за указанный период.")
    public ResponseEntity<byte[]> exportToExcel(@RequestParam(required = false) String dateFrom,
                                                @RequestParam(required = false) String dateUntil) {
        try {
            byte[] excelDate = exportToExcelService.exportExcel(dateFrom, dateUntil);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "Doctors Schedule.xlsx");
            return new ResponseEntity<>(excelDate, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/globalSearch")
    @Operation(summary = "Метод для поиска расписания врачей по ФИО врача и позиции",
            description = """
                       Метод позволяет увидеть расписание врачей по поиску
                         """)
    public List<ScheduleResponse> globalSearch(@RequestParam String word){
        return scheduleService.globalSearch(word);
    }
}