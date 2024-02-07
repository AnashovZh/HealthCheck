package com.example.healthcheckb10.exceptions.handler;

import com.example.healthcheckb10.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<String> errors = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return ExceptionResponse
                .builder()
                .message(errors.toString())
                .httpStatus(HttpStatus.CONFLICT)
                .exceptionClassName(e.getClass().getSimpleName())
                .build();
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ExceptionResponse handleConstraintViolationException(ConstraintViolationException e) {
        StringBuilder errorMessage = new StringBuilder("Ошибка валидации: ");
        e.getConstraintViolations().forEach(violation -> errorMessage.append(violation.getMessage()).append("; "));
        return ExceptionResponse.builder()
                .message(errorMessage.toString())
                .httpStatus(HttpStatus.CONFLICT)
                .exceptionClassName(e.getClass().getSimpleName())
                .build();
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse alreadyExistsException(AlreadyExistsException e){
        return ExceptionResponse.builder()
                .message(e.getMessage())
                .exceptionClassName(e.getClass().getSimpleName())
                .httpStatus(HttpStatus.CONFLICT)
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse notFoundException(NotFoundException e){
        return ExceptionResponse.builder()
                .message(e.getMessage())
                .exceptionClassName(e.getClass().getSimpleName())
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler(BadCredentialException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse badCredentialException(BadCredentialException e){
        return ExceptionResponse.builder()
                .message(e.getMessage())
                .exceptionClassName(e.getClass().getSimpleName())
                .httpStatus(HttpStatus.FORBIDDEN)
                .build();
    }
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse badRequestException(BadRequestException e){
        return ExceptionResponse.builder()
                .message(e.getMessage())
                .exceptionClassName(e.getClass().getSimpleName())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }
}