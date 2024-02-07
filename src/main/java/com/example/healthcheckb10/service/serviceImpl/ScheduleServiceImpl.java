package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.dto.schedule.request.CreateDoctorScheduleRequest;
import com.example.healthcheckb10.dto.schedule.responce.ScheduleResponse;
import com.example.healthcheckb10.entities.Department;
import com.example.healthcheckb10.entities.Doctor;
import com.example.healthcheckb10.entities.Schedule;
import com.example.healthcheckb10.entities.Timesheet;
import com.example.healthcheckb10.enums.Day;
import com.example.healthcheckb10.enums.Facility;
import com.example.healthcheckb10.exceptions.AlreadyExistsException;
import com.example.healthcheckb10.exceptions.BadRequestException;
import com.example.healthcheckb10.exceptions.NotFoundException;
import com.example.healthcheckb10.repositories.DepartmentRepository;
import com.example.healthcheckb10.repositories.DoctorRepository;
import com.example.healthcheckb10.repositories.ScheduleRepository;
import com.example.healthcheckb10.repositories.TimesheetRepository;
import com.example.healthcheckb10.repositories.jdbcTemplate.ScheduleDao;
import com.example.healthcheckb10.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final TimesheetRepository timesheetRepository;
    private final ScheduleDao scheduleDao;

    @Override
    public SimpleResponse createSchedule(CreateDoctorScheduleRequest createDoctorScheduleRequest) {
        Department department = departmentRepository.findDepartmentByFacilityName(Facility.valueOf(createDoctorScheduleRequest.departmentName()))
                .orElseThrow(() -> {
                    log.error("Отделение под названием %s не найдено!"
                            .formatted(createDoctorScheduleRequest.departmentName()));
                    return new NotFoundException("Отделение под названием %s не найдено!"
                            .formatted(createDoctorScheduleRequest.departmentName()));
                });
        Doctor doctor = doctorRepository.findDoctorByDepartmentAndId(department, createDoctorScheduleRequest.doctorId())
                .orElseThrow(() -> {
                    log.error("Специалист под идентификатором %s не работает в отделении - %s!"
                            .formatted(createDoctorScheduleRequest.doctorId(), department.getFacilityName()));
                    return new NotFoundException("Специалист под идентификатором %s не работает в отделении - %s!"
                            .formatted(createDoctorScheduleRequest.doctorId(), department.getFacilityName()));
                });
        if (scheduleRepository.existsByDoctor(doctor)) {
            throw new AlreadyExistsException("Расписание специалиста - %s %s уже существует!"
                    .formatted(doctor.getFirstName(), doctor.getLastName()));
        }
        LocalDate currentDate = createDoctorScheduleRequest.startDateOfWork();
        LocalDate endDate = createDoctorScheduleRequest.endDateOfWork();
        Map<Day, Boolean> dayOfWeek = createDoctorScheduleRequest.dayOfWeek();
        LocalTime startTime = LocalTime.parse(createDoctorScheduleRequest.startTimeOfWork());
        LocalTime endTime = LocalTime.parse(createDoctorScheduleRequest.endTimeOfWork());
        int interval = createDoctorScheduleRequest.intervalInMinutes();

        if (interval != 30 && interval != 45 && interval != 60 && interval != 90) {
            throw new BadRequestException("Интервал времени Вы можете задать только 30, 45, 60 или 90 минут!");
        }

        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);
        schedule.setDepartment(department);
        schedule.setStartDateOfWork(currentDate);
        schedule.setEndDateOfWork(endDate);
        schedule.setStartBreakTime(LocalTime.parse(createDoctorScheduleRequest.startBreakTime()));
        schedule.setEndBreakTime(LocalTime.parse(createDoctorScheduleRequest.endBreakTime()));
        schedule.setIntervalInMinutes(interval);
        schedule.setDayOfWeek(dayOfWeek);
        scheduleRepository.save(schedule);
        Map<Day, Boolean> newDayOfWeek = new HashMap<>(dayOfWeek);
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            for (Map.Entry<Day, Boolean> entry : newDayOfWeek.entrySet()) {
                if (currentDate.getDayOfWeek() == entry.getKey().getDayOfWeek() && entry.getValue()) {
                    LocalTime currentTime = startTime;
                    while (currentTime.isBefore(endTime) && (currentTime.plusMinutes(15).isBefore(endTime))) {
                        Timesheet timesheet = new Timesheet();
                        timesheet.setSchedule(schedule);
                        timesheet.setStartTimeOfConsultation(currentTime);
                        timesheet.setEndTimeOfConsultation(currentTime.plusMinutes(interval));
                        timesheet.setIsAvailable(true);
                        timesheet.setDateOfConsultation(currentDate);
                        timesheetRepository.save(timesheet);
                        currentTime = currentTime.plusMinutes(interval);
                    }

                    List<Timesheet> all = timesheetRepository.findAll();
                    for (Timesheet timesheet : all) {
                        LocalTime startTimeOfConsultation = timesheet.getStartTimeOfConsultation();
                        LocalTime endTimeOfConsultation = timesheet.getEndTimeOfConsultation();
                        if (interval == 60
                                && startTimeOfConsultation.equals(schedule.getStartBreakTime())
                                && endTimeOfConsultation.equals(schedule.getEndBreakTime())) {
                            timesheet.setIsAvailable(false);
                            timesheetRepository.save(timesheet);
                        } else if (interval == 30
                                && ((startTimeOfConsultation.equals(schedule.getStartBreakTime())
                                && endTimeOfConsultation.isAfter(schedule.getStartBreakTime()))
                                || (startTimeOfConsultation.isAfter(schedule.getStartBreakTime())
                                && endTimeOfConsultation.equals(schedule.getEndBreakTime())))) {
                            timesheet.setIsAvailable(false);
                            timesheetRepository.save(timesheet);
                        } else if (interval == 90
                                && startTimeOfConsultation.minusMinutes(30).equals(schedule.getStartBreakTime())
                                && endTimeOfConsultation.minusMinutes(60).equals(schedule.getEndBreakTime())) {
                            timesheet.setIsAvailable(false);
                            timesheetRepository.save(timesheet);
                        } else if (interval == 45
                                && startTimeOfConsultation.isAfter(schedule.getStartBreakTime())
                                && endTimeOfConsultation.minusMinutes(15).equals(schedule.getEndBreakTime())) {
                            timesheet.setIsAvailable(false);
                            timesheetRepository.save(timesheet);
                        }
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }
        log.info("Успешно сохранено расписание специалиста - %s %s ".formatted(doctor.getFirstName(), doctor.getLastName()));
        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message("Успешно сохранено расписание специалиста - %s %s ".formatted(
                        doctor.getFirstName(),
                        doctor.getLastName()))
                .build();
    }

    @Override
    public List<ScheduleResponse> getAll(String dateFrom, String dateUntil) {
        if (dateFrom == null && dateUntil == null) {
            return scheduleDao.getAll();
        } else {
            return scheduleDao.filterDate(dateFrom, dateUntil);
        }
    }

    @Override
    public List<ScheduleResponse> globalSearch(String word) {
        return scheduleDao.globalSearch(word);
    }
}