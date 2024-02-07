package com.example.healthcheckb10.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_gen")
    @SequenceGenerator(
            name = "user_gen",
            sequenceName = "user_seq",
            initialValue = 11,
            allocationSize = 1)
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserAccount userAccount;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Result> results;

    public void addResult(Result result){
        if (result == null){
            results = new ArrayList<>();
        }
        results.add(result);
    }
}