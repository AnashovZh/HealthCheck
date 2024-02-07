package com.example.healthcheckb10.dto.application;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SearchApplicationResponse {
    private Long id;
    private String name;
    private String createdAt;
    private String phoneNumber;
    private Boolean processed;
    public SearchApplicationResponse(Long id, String name, String createdAt, String phoneNumber, Boolean processed) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.phoneNumber = phoneNumber;
        this.processed = processed;
    }
}