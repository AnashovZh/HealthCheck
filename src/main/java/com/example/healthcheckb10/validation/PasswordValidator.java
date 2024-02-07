package com.example.healthcheckb10.validation;

import com.example.healthcheckb10.exceptions.NotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s.length() < 4 || !s.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")){
            throw new NotFoundException("Пожалуйста, придумайте более надежный пароль!" +
                    " (должен содержать хотя бы одну заглавную букву, одну строчную букву и одну цифру)");
        }else {
            return true;
        }
    }
}