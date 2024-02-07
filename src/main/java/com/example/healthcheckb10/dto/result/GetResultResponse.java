package com.example.healthcheckb10.dto.result;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GetResultResponse {
    private Long id;
    private String pdgFileCheque;

    public GetResultResponse(Long id,String pdgFileCheque) {
        this.id = id;
        this.pdgFileCheque = pdgFileCheque;
    }
}
