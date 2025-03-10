package com.example.validator.country;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;
import java.util.Set;

public class CountryValidator implements ConstraintValidator<ValidCountry, String> {

    private static final Set<String> ISO_COUNTRIES = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA3);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            return ISO_COUNTRIES.contains(value.toUpperCase());
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }
}
