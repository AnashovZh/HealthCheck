package com.example.healthcheckb10.api;

import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.dto.schedule.request.ScheduleDayUpdateRequest;
import com.example.healthcheckb10.dto.timesheet.TimesheetResponse;
import com.example.healthcheckb10.service.TimesheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
@Tag(name = "Timesheet Api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TimesheetApi {
    private final TimesheetService timesheetService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    @Operation(summary = "Метод для обновления одного дня расписания",
            description = """
                       Метод позволяет обновить времена определенного дня расписания врача
                       """)
    public SimpleResponse updateDay(@RequestParam Long doctorId,
                                    @RequestParam LocalDate scheduleDate,
                                    @RequestBody @Valid List<ScheduleDayUpdateRequest> timesToUpdate){
        return timesheetService.updateDay(doctorId, scheduleDate, timesToUpdate);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getTimesheetOfDoctorByScheduleIDAndDate")
    @Operation(summary = "Метод для выборки графика по идентификатору расписания и даты",
            description = """
                       Метод позволяет увидеть график определенного врача 
                       """)
    public TimesheetResponse getById(@RequestParam Long scheduleId,
                                     @RequestParam LocalDate date){
        return timesheetService.getById(scheduleId,date);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(summary = "Метод для установления по шаблону ",
            description = """
                       Метод позволяет установить график на пустой день врача                 
                       """)
    public SimpleResponse setTemplate(@RequestParam Long doctorId,
                                      @RequestParam LocalDate scheduleDate,
                                      @RequestBody @Valid List<ScheduleDayUpdateRequest>timesToSet){
        return timesheetService.setTemplate(doctorId,scheduleDate,timesToSet);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    @Operation(summary = "Метод для удаления времени записи",
            description = """
                       Метод позволяет удалить определенное время записи из временных интервалов определенного дня и графика             
                       """)
    public SimpleResponse deleteTime(@RequestParam Long doctorId,
                                     @RequestParam LocalDate scheduleDate,
                                     @RequestParam  String time){
        return timesheetService.deleteTime(doctorId,scheduleDate, LocalTime.parse(time));
    }
}