package com.example.healthcheckb10.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface PhoneNumberValidation {
    String message() default "Номер телефона должен начинаться с +996 и содержать 13 цифр !";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}