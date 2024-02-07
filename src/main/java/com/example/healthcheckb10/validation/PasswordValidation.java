package com.example.healthcheckb10.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface PasswordValidation {
    String message() default "Пожалуйста, придумайте более надежный пароль!" +
                             "(должен содержать хотя бы букву и цифру) ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}