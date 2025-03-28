package com.example.model.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.QueryParam;

public record SimpleBinAPI(

        @QueryParam("binNumber")
        @NotBlank(message = "Bin may not be blank")
        @Pattern(regexp = "^[0-9]{8}$", message = "Bin should contain only 8 digits")
        String binNumber
) {
}
