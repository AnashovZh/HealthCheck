package com.example.healthcheckb10.entities;

import com.example.healthcheckb10.enums.Facility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "departments")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "department_gen")
    @SequenceGenerator(
            name = "department_gen",
            sequenceName = "department_seq",
            initialValue = 23,
            allocationSize = 1)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Facility facilityName;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Doctor> doctors;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Result>results;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Schedule>schedules;
}