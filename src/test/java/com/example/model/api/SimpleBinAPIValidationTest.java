package com.example.model.api;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleBinAPIValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidBinNumber() {
        SimpleBinAPI simpleBinAPI = new SimpleBinAPI();
        simpleBinAPI.setBinNumber("12345678");

        Set<ConstraintViolation<SimpleBinAPI>> violations = validator.validate(simpleBinAPI);
        assertTrue(violations.isEmpty(), "Expected no validation errors for a valid bin number");
    }

    @Test
    void testInvalidBinNumberTooShort() {
        SimpleBinAPI simpleBinAPI = new SimpleBinAPI();
        simpleBinAPI.setBinNumber("12345");

        Set<ConstraintViolation<SimpleBinAPI>> violations = validator.validate(simpleBinAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for bin length");
    }

    @Test
    void testInvalidBinNumberNonNumeric() {
        SimpleBinAPI simpleBinAPI = new SimpleBinAPI();
        simpleBinAPI.setBinNumber("abcdefgh");

        Set<ConstraintViolation<SimpleBinAPI>> violations = validator.validate(simpleBinAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for non-numeric bin");
    }

    @Test
    void testInvalidBinNumberBlank() {
        SimpleBinAPI simpleBinAPI = new SimpleBinAPI();
        simpleBinAPI.setBinNumber("        ");

        Set<ConstraintViolation<SimpleBinAPI>> violations = validator.validate(simpleBinAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for blank bin");
    }

    @Test
    void testInvalidBinNumberNull() {
        SimpleBinAPI simpleBinAPI = new SimpleBinAPI();
        simpleBinAPI.setBinNumber(null);

        Set<ConstraintViolation<SimpleBinAPI>> violations = validator.validate(simpleBinAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for null bin");
    }
}