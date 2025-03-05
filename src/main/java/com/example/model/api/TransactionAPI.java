package com.example.model.api;

import com.example.logging.MaskedLog;
import com.example.validator.ValidCountry;
import com.example.validator.ValidCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionAPI {
    private static final String BIN_PATTERN = "^[0-9]{8}$";

    @NotBlank(message="binNumber may not be blank")
    @Pattern(regexp = BIN_PATTERN, message = "Bin should contains only 8 digits")
    @MaskedLog
    private String binNumber;

    @Positive(message = "amount cannot be negative")
    @NotNull
    private long amount;

    @NotBlank(message = "currency may not be blank")
    @ValidCurrency(message = "Currency must be 3 uppercase letters long ISO_4217 code")
    private String currency;

    @NotBlank(message = "userLocation may not be blank")
    @ValidCountry
    private String userLocation;
}
