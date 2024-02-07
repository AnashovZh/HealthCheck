    package com.example.healthcheckb10.entities;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import java.time.LocalDate;
    import java.time.LocalTime;

    @Entity
    @Table(name = "timesheets")
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Timesheet {
        @Id
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "timesheet_gen")
        @SequenceGenerator(
                name = "timesheet_gen",
                sequenceName = "timesheet_seq",
                initialValue = 51,
                allocationSize = 1)
        private Long id;
        private LocalTime startTimeOfConsultation;
        private LocalTime endTimeOfConsultation;
        private LocalDate dateOfConsultation;
        private Boolean isAvailable;
        @ManyToOne(cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE,
                CascadeType.DETACH,
                CascadeType.REFRESH})
        private Schedule schedule;
    }