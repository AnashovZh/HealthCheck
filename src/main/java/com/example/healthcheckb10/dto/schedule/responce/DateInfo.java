package com.example.healthcheckb10.dto.schedule.responce;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DateInfo {
    private String dateDay;
    private String dayOfWeek;
    private List<TimeSlot> timeIntervals;
    public DateInfo(String dateDay, String dayOfWeek, List<TimeSlot> timeIntervals) {
        this.dateDay = dateDay;
        this.dayOfWeek = dayOfWeek;
        this.timeIntervals = timeIntervals;
    }

    public DateInfo(String string, Object o) {
    }
}