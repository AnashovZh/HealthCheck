package com.example.healthcheckb10.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "doctors")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "doctor_gen")
    @SequenceGenerator(
            name = "doctor_gen",
            sequenceName = "doctor_seq",
            initialValue = 65,
            allocationSize = 1)
    private Long id;
    private String firstName;
    private String lastName;
    private String image;
    private String position;
    private String description;
    private Boolean isActive;

    @OneToOne(mappedBy = "doctor",cascade = CascadeType.ALL)
    private Schedule schedule;

    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH})
    private Department department;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Appointment> appointments;
}