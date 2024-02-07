package com.example.healthcheckb10.validation;

import com.example.healthcheckb10.exceptions.BadCredentialException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MyEmailValidator implements ConstraintValidator<MyEmailValidation,String> {
    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email.endsWith("@gmail.com") || email.endsWith("@mail.ru")){
            return true;
        }else {
            throw new BadCredentialException("Неверный формат почты!");
        }
    }
}