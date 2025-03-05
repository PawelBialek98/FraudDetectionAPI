package com.example.model.api;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionAPIValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidTransaction() {
        TransactionAPI transactionAPI = new TransactionAPI();
        transactionAPI.setBinNumber("12345678");
        transactionAPI.setAmount(100L);
        transactionAPI.setCurrency("USD");
        transactionAPI.setUserLocation("USA");

        Set<ConstraintViolation<TransactionAPI>> violations = validator.validate(transactionAPI);
        assertTrue(violations.isEmpty(), "Expected no validation errors for a valid transaction");
    }

    @Test
    void testInvalidBinNumber() {
        TransactionAPI transactionAPI = new TransactionAPI();
        transactionAPI.setBinNumber("12345");
        transactionAPI.setAmount(100L);
        transactionAPI.setCurrency("USD");
        transactionAPI.setUserLocation("USA");

        Set<ConstraintViolation<TransactionAPI>> violations = validator.validate(transactionAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for invalid bin number");
        assertEquals(1, violations.size());
    }

    @Test
    void testNegativeAmount() {
        TransactionAPI transactionAPI = new TransactionAPI();
        transactionAPI.setBinNumber("12345678");
        transactionAPI.setAmount(-50L);
        transactionAPI.setCurrency("USD");
        transactionAPI.setUserLocation("USA");

        Set<ConstraintViolation<TransactionAPI>> violations = validator.validate(transactionAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for negative amount");
        assertEquals(1, violations.size());
    }

    @Test
    void testInvalidCurrency() {
        TransactionAPI transactionAPI = new TransactionAPI();
        transactionAPI.setBinNumber("12345678");
        transactionAPI.setAmount(100L);
        transactionAPI.setCurrency("US");
        transactionAPI.setUserLocation("USA");

        Set<ConstraintViolation<TransactionAPI>> violations = validator.validate(transactionAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for invalid currency");
        assertEquals(1, violations.size());
    }

    @Test
    void testInvalidUserLocation() {
        TransactionAPI transactionAPI = new TransactionAPI();
        transactionAPI.setBinNumber("12345678");
        transactionAPI.setAmount(100L);
        transactionAPI.setCurrency("USD");
        transactionAPI.setUserLocation("");

        Set<ConstraintViolation<TransactionAPI>> violations = validator.validate(transactionAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for blank user location");
        assertEquals(2, violations.size()); //@NotBlank and @ValidCountry
    }
}
