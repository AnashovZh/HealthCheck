package com.example.healthcheckb10.dto.appointment.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DoctorWithFreeDatesAndTimes {
    private Long doctorId;
    private String freeDate;
    private List<String> freeTimes;

    public DoctorWithFreeDatesAndTimes(Long doctorId, String freeDate, List<String> freeTimes) {
        this.doctorId = doctorId;
        this.freeDate = freeDate;
        this.freeTimes = freeTimes;
    }
}
