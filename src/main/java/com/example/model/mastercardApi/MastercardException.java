package com.example.model.mastercardApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MastercardException(
        @JsonProperty("Errors") MastercardExceptionInner errors
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MastercardExceptionInner(
            @JsonProperty("Error") List<MastercardExceptionValues> error
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MastercardExceptionValues(
            @JsonProperty("Source") String source,
            @JsonProperty("ReasonCode") String reasonCode,
            @JsonProperty("Description") String description,
            @JsonProperty("Recoverable") boolean recoverable,
            @JsonProperty("Details") String details
    ) {
    }
}