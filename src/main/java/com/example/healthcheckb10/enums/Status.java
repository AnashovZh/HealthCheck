package com.example.healthcheckb10.enums;

import lombok.Getter;

@Getter
public enum Status {
    CONFIRMED("Подтвержден"),
    COMPLETED("Завершен"),
    CANCELED("Отменен");

    private final String russianName;
    Status(String russianName) {
        this.russianName = russianName;
    }
}