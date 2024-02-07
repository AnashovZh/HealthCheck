package com.example.healthcheckb10.exceptions;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ExceptionResponse(
        String exceptionClassName,
        String message,
        HttpStatus httpStatus)  {
}