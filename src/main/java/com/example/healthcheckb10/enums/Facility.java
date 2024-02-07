package com.example.healthcheckb10.enums;

import lombok.Getter;

@Getter
public enum Facility {
    ALLERGOLOGY("Аллергология"),
    VACCINATION("Вакцинация"),
    GYNECOLOGY("Гинекология"),
    CARDIOLOGY("Кардиология"),
    NEUROSURGERY("Нейрохирургия"),
    ORTHOPEDICS("Ортопедия"),
    OPHTHALMOLOGY("Офтальмология"),
    PSYCHOTHERAPY("Психотерапия"),
    RHEUMATOLOGY("Ревматология"),
    UROLOGY("Урология"),
    ENDOCRINOLOGY("Эндокринология"),
    ANESTHESIOLOGY("Анестезиология"),
    GASTROENTEROLOGY("Гастроэнтерология"),
    DERMATOLOGY("Дерматология"),
    NEUROLOGY("Неврология"),
    ONCOLOGY("Онкология"),
    OTOLARYNGOLOGY("Отоларингология"),
    PROCTOLOGY("Проктология"),
    PULMONOLOGY("Пульмонология"),
    THERAPY("Терапия"),
    PHLEBOLOGY("Флебология"),
    PHYSIOTHERAPY("Физиотерапия");

    private final String russianName;

    Facility(String russianName) {
        this.russianName = russianName;
    }
}