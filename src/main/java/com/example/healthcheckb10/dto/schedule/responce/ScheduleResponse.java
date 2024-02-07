package com.example.healthcheckb10.dto.schedule.responce;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
@Getter
public class ScheduleResponse {
    private Long scheduleId;
    private Long doctorId;
    private String doctorImage;
    private String doctorFullName;
    private String doctorPosition;
    private List<DateInfo> dateDayTimeInfos;
    public ScheduleResponse(Long scheduleId,Long doctorId, String doctorImage, String doctorFullName, String doctorPosition, List<DateInfo> dateDayTimeInfos) {
        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.doctorImage = doctorImage;
        this.doctorFullName = doctorFullName;
        this.doctorPosition = doctorPosition;
        this.dateDayTimeInfos = dateDayTimeInfos;
    }
}