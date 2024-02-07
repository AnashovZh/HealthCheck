package com.example.healthcheckb10.repositories;

import com.example.healthcheckb10.entities.Schedule;
import com.example.healthcheckb10.entities.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    @Query("select t.isAvailable from Timesheet t where t.schedule.id=:sheduleId and t.dateOfConsultation =:date and t.startTimeOfConsultation =:time ")
    Boolean booked(Long sheduleId, LocalDate date, LocalTime time);
    @Query("select t from  Timesheet  t where t.schedule.doctor.id= :doctorId and t.dateOfConsultation= :dateOfVisiting and t.startTimeOfConsultation= :time")
    Timesheet findTimesheet(Long doctorId, LocalDate dateOfVisiting, LocalTime time);
    List<Timesheet> findByScheduleAndDateOfConsultation(Schedule schedule, LocalDate dateOfConsultation);
    void deleteAllByScheduleAndDateOfConsultation(Schedule schedule, LocalDate scheduleDate);
    boolean existsByStartTimeOfConsultation(LocalTime startTimeOfConsultation);
}