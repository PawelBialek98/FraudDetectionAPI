package com.example.model.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
        TransactionAPI transactionAPI = TransactionAPI.builder()
                .binNumber("12345678")
                .amount(100L)
                .currency("USD")
                .userLocation("USA")
                .build();

        Set<ConstraintViolation<TransactionAPI>> violations = validator.validate(transactionAPI);
        assertTrue(violations.isEmpty(), "Expected no validation errors for a valid transaction");
    }

    @Test
    void testInvalidBinNumber() {
        TransactionAPI transactionAPI = TransactionAPI.builder()
                .binNumber("12345")
                .amount(100L)
                .currency("USD")
                .userLocation("USA")
                .build();

        Set<ConstraintViolation<TransactionAPI>> violations = validator.validate(transactionAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for invalid bin number");
        assertEquals(1, violations.size());
    }

    @Test
    void testNegativeAmount() {
        TransactionAPI transactionAPI = TransactionAPI.builder()
                .binNumber("12345678")
                .amount(-50L)
                .currency("USD")
                .userLocation("USA")
                .build();

        Set<ConstraintViolation<TransactionAPI>> violations = validator.validate(transactionAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for negative amount");
        assertEquals(1, violations.size());
    }

    @Test
    void testInvalidCurrency() {
        TransactionAPI transactionAPI = TransactionAPI.builder()
                .binNumber("12345678")
                .amount(100L)
                .currency("US")
                .userLocation("USA")
                .build();

        Set<ConstraintViolation<TransactionAPI>> violations = validator.validate(transactionAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for invalid currency");
        assertEquals(1, violations.size());
    }

    @Test
    void testInvalidUserLocation() {
        TransactionAPI transactionAPI = TransactionAPI.builder()
                .binNumber("12345678")
                .amount(100L)
                .currency("USD")
                .userLocation("")
                .build();

        Set<ConstraintViolation<TransactionAPI>> violations = validator.validate(transactionAPI);
        assertFalse(violations.isEmpty(), "Expected validation error for blank user location");
        assertEquals(2, violations.size()); //@NotBlank and @ValidCountry
    }
}
