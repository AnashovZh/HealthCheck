package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.dto.schedule.request.ScheduleDayUpdateRequest;
import com.example.healthcheckb10.dto.timesheet.TimesheetResponse;
import com.example.healthcheckb10.entities.Doctor;
import com.example.healthcheckb10.entities.Schedule;
import com.example.healthcheckb10.entities.Timesheet;
import com.example.healthcheckb10.exceptions.BadRequestException;
import com.example.healthcheckb10.exceptions.NotFoundException;
import com.example.healthcheckb10.repositories.DoctorRepository;
import com.example.healthcheckb10.repositories.ScheduleRepository;
import com.example.healthcheckb10.repositories.TimesheetRepository;
import com.example.healthcheckb10.repositories.jdbcTemplate.TimesheetDao;
import com.example.healthcheckb10.service.TimesheetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimesheetServiceImpl implements TimesheetService {
    private final TimesheetRepository timesheetRepository;
    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;
    private final TimesheetDao timesheetDao;

    @Transactional
    @Override
    public SimpleResponse updateDay(Long doctorId, LocalDate scheduleDate, List<ScheduleDayUpdateRequest> timesToUpdate) {
        Doctor doctor = findDoctorById(doctorId);
        Schedule schedule = getScheduleByDoctor(doctor);
        List<Timesheet> consultations = timesheetRepository.findByScheduleAndDateOfConsultation(schedule, scheduleDate);
        if (consultations.isEmpty()) {
            log.error("Врач с идентификатором %s не работает в день %s ".formatted(doctorId, scheduleDate));
            throw new BadRequestException("Врач с идентификатором %s не работает в день %s ".formatted(doctorId, scheduleDate));
        }
        if(timesheetRepository.existsByStartTimeOfConsultation(LocalTime.parse(timesToUpdate.get(0).getNewStartTime()))){
            log.error("У врача с идентификатором %s уже есть окошко в %s часов в %s день".formatted(doctorId, timesToUpdate.get(0).getNewStartTime(),scheduleDate));
            throw new BadRequestException("У врача с идентификатором %s уже есть окошко в %s часов в %s день ".formatted(doctorId, timesToUpdate.get(0).getNewStartTime(),scheduleDate));
        }
        for (ScheduleDayUpdateRequest request : timesToUpdate) {
            Timesheet newTimesheet = new Timesheet();
            newTimesheet.setSchedule(schedule);
            newTimesheet.setDateOfConsultation(scheduleDate);
            newTimesheet.setStartTimeOfConsultation(LocalTime.parse(request.getNewStartTime()));
            newTimesheet.setEndTimeOfConsultation(LocalTime.parse(request.getNewEndTime()));
            newTimesheet.setIsAvailable(true);
            timesheetRepository.save(newTimesheet);
        }
        log.info("Обновлено время работы для врача c идентификатором %s в день %s ".formatted(doctorId, scheduleDate));
        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message("Обновлено время работы для врача с идентификатором %s в день %s ".formatted(doctorId, scheduleDate))
                .build();
    }

    @Override
    public TimesheetResponse getById(Long scheduleId, LocalDate date) {
        return timesheetDao.getById(scheduleId, date)
                .orElseThrow(() -> {
                    log.error("График с идентификатором %s и датой %s не найден! ".formatted(scheduleId, date));
                    return new NotFoundException("График с идентификатором %s и датой %s не найден! ".formatted(scheduleId, date));
                });
    }

    @Override
    public SimpleResponse setTemplate(Long doctorId, LocalDate scheduleDate, List<ScheduleDayUpdateRequest> timesToSet) {
        Doctor doctor = findDoctorById(doctorId);
        Schedule schedule = getScheduleByDoctor(doctor);
        List<Timesheet> consultations = timesheetRepository.findByScheduleAndDateOfConsultation(schedule, scheduleDate);
        if (!consultations.isEmpty()) {
            log.error("У врача с идентификатором %s уже существует график в день %s ".formatted(doctorId, scheduleDate));
            throw new BadRequestException("У врача с идентификатором %s уже существует график в день %s ".formatted(doctorId, scheduleDate));
        }
        for (int i = 0; i < timesToSet.size(); i++) {
            Timesheet consultation = new Timesheet();
            consultation.setSchedule(schedule);
            consultation.setStartTimeOfConsultation(LocalTime.parse(timesToSet.get(i).getNewStartTime()));
            consultation.setEndTimeOfConsultation(LocalTime.parse(timesToSet.get(i).getNewEndTime()));
            consultation.setIsAvailable(true);
            consultation.setDateOfConsultation(scheduleDate);
            timesheetRepository.save(consultation);
        }
        log.info("Установлено время работы для врача c идентификатором %s в день %s ".formatted(doctorId, scheduleDate));
        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message("Установлено время работы для врача с идентификатором %s в день %s ".formatted(doctorId, scheduleDate))
                .build();
    }

    @Override
    public SimpleResponse deleteTime(Long doctorId, LocalDate scheduleDate, LocalTime time) {
        Doctor doctor = findDoctorById(doctorId);
        Schedule schedule = getScheduleByDoctor(doctor);
        List<Timesheet> consultations = timesheetRepository.findByScheduleAndDateOfConsultation(schedule, scheduleDate);
        if (consultations.isEmpty()) {
            log.error("Врач с идентификатором %s не работает в день %s ".formatted(doctorId, scheduleDate));
            throw new BadRequestException("Врач с идентификатором %s не работает в день %s ".formatted(doctorId, scheduleDate));
        }
        boolean timeSlotFoundAndDeleted = false;
        for (Timesheet timesheet : consultations) {
            if (timesheet.getStartTimeOfConsultation().equals(time)) {
                timesheetRepository.delete(timesheet);
                timeSlotFoundAndDeleted=true;
                break;
            }
        }
        if(!timeSlotFoundAndDeleted){
            throw new BadRequestException("Время %s не найдено!".formatted(time));
        }
        log.info("Удалено время работы для врача c идентификатором %s в день %s в %s час!".formatted(doctorId, scheduleDate, time));
        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message("Удалено время работы для врача с идентификатором %s в день %s  в %s час!".formatted(doctorId, scheduleDate, time))
                .build();
    }

    private Schedule getScheduleByDoctor(Doctor doctor) {
        return scheduleRepository.findByDoctor(doctor)
                .orElseThrow(() -> {
                    log.error("Расписание врача с идентификатором %s не найден! ".formatted(doctor.getId()));
                    return new NotFoundException("Расписание врача с идентификатором %s не найден! ".formatted(doctor.getId()));
                });
    }

    private Doctor findDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId).orElseThrow(() -> {
            log.error("Доктор с идентификатором %s не найден! ".formatted(doctorId));
            return new NotFoundException(
                    "Доктор с идентификатором %s не найден! ".formatted(doctorId));
        });
    }
}