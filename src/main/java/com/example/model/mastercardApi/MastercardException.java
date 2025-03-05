package com.example.model.mastercardApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MastercardException {

    @JsonProperty("Errors")
    public MastercardExceptionInner errors;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MastercardExceptionInner{
        @JsonProperty("Error")
        public List<MastercardExceptionValues> error;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MastercardExceptionValues {
        @JsonProperty("Source")
        public String source;
        @JsonProperty("ReasonCode")
        public String reasonCode;
        @JsonProperty("Description")
        public String description;
        @JsonProperty("Recoverable")
        public boolean recoverable;
        @JsonProperty("Details")
        public String details;
    }
}