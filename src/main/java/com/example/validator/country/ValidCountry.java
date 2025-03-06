package com.example.validator.country;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CountryValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface  ValidCountry {
    String message() default "Invalid country code. Must be a valid ISO 639";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}