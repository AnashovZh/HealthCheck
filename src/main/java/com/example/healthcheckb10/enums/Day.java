package com.example.healthcheckb10.enums;

public enum Day {
    MONDAY(java.time.DayOfWeek.MONDAY),
    TUESDAY(java.time.DayOfWeek.TUESDAY),
    WEDNESDAY(java.time.DayOfWeek.WEDNESDAY),
    THURSDAY(java.time.DayOfWeek.THURSDAY),
    FRIDAY(java.time.DayOfWeek.FRIDAY),
    SATURDAY(java.time.DayOfWeek.SATURDAY),
    SUNDAY(java.time.DayOfWeek.SUNDAY);
    private final java.time.DayOfWeek javaTimeDay;
    Day(java.time.DayOfWeek javaTimeDay) {
        this.javaTimeDay = javaTimeDay;
    }
    public java.time.DayOfWeek getDayOfWeek() {
        return javaTimeDay;
    }
}