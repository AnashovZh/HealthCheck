package com.example.healthcheckb10.dto.result;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Builder
public class ResultResponse {
    private Long id;
    private Long userid;
    private String departmentName;
    private LocalDate dateOfUploadingResult;
    private String timeOfUploadingResult;
    private String orderNumber;
    private String pdgFileCheque;

    public ResultResponse(Long id, Long userid, String departmentName, LocalDate dateOfUploadingResult, String timeOfUploadingResult, String orderNumber, String pdgFileCheque) {
        this.id = id;
        this.userid = userid;
        this.departmentName = departmentName;
        this.dateOfUploadingResult = dateOfUploadingResult;
        this.timeOfUploadingResult = timeOfUploadingResult;
        this.orderNumber = orderNumber;
        this.pdgFileCheque = pdgFileCheque;
    }
}