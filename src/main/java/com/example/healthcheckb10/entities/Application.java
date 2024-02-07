package com.example.healthcheckb10.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "applications")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "application_gen")
    @SequenceGenerator(
            name = "application_gen",
            sequenceName = "application_seq",
            allocationSize = 1,
            initialValue = 11
    )
    private Long id;
    private String firstName;
    private LocalDate creatingApplicationDate;
    private String phoneNumber ;
    private Boolean processed;
}