package com.example.healthcheckb10.dto.schedule.responce;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TimeSlot {
    private String startTime;
    private String endTime;
    private String isAvailable;
}