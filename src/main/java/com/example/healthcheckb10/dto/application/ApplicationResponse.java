package com.example.healthcheckb10.dto.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Getter
@Setter
public class ApplicationResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private Boolean processed;
    private String createdAt;
    public ApplicationResponse(Long id, String name, String phoneNumber, Boolean processed, java.sql.Date createdAt) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.processed = processed;
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = Instant.ofEpochMilli(createdAt.getTime());
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.createdAt = dateTimeFormatter.format(zonedDateTime);
    }
}