package com.example.model.api;

import com.example.validator.country.ValidCountry;
import com.example.validator.currency.ValidCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Builder
public record TransactionAPI(

        @NotBlank(message = "binNumber may not be blank")
        @Pattern(regexp = BIN_PATTERN, message = "Bin should contains only 8 digits")
        @Schema(description = "8 digit long BIN number", examples = {"12345678"})
        String binNumber,

        @Positive(message = "amount cannot be negative")
        @NotNull
        @Schema(description = "Related to currency. Represents the total amount in a given currency (including hundredths/tenths - if any) ", examples = {"10000"})
        long amount,

        @NotBlank(message = "currency may not be blank")
        @ValidCurrency(message = "Currency must be 3 uppercase letters long ISO_4217 code")
        @Schema(description = "3 letter long ISO 4217 code", examples = {"PLN"})
        String currency,

        @NotBlank(message = "userLocation may not be blank")
        @ValidCountry
        @Schema(description = "3 letter long country code", examples = {"POL"})
        String userLocation
) {
    public static final String BIN_PATTERN = "^[0-9]{8}$";
}
