package com.example.healthcheckb10.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "results")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "result_gen")
    @SequenceGenerator(
            name = "result_gen",
            sequenceName = "result_seq",
            initialValue = 11,
            allocationSize = 1)
    private Long id;
    private LocalDate dateOfUploadingResult;
    private LocalTime timeOfUploadingResult;
    private String resultNumber;
    private String pdgFileCheque;
    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH})
    private User user;
    @ManyToOne(cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE,
                CascadeType.DETACH,
                CascadeType.REFRESH})
    private Department department;
}