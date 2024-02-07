package com.example.healthcheckb10.entities;

import com.example.healthcheckb10.enums.Day;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "schedules")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "schedule_gen")
    @SequenceGenerator(
            name = "schedule_gen",
            sequenceName = "schedule_seq",
            initialValue = 65,
            allocationSize = 1)
    private Long id;
    private LocalDate startDateOfWork;
    private LocalDate endDateOfWork;
    private LocalTime startBreakTime;
    private LocalTime endBreakTime;
    private int intervalInMinutes;
    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Day, Boolean> dayOfWeek;
    @OneToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH})
    private Doctor doctor;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<Timesheet> timesheets;

    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH})
    private Department department;
}