package com.example.healthcheckb10.dto.timesheet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TimesheetResponse {
    private String position;
    private String fullName;
    private String date;
    private List<String>times;
}