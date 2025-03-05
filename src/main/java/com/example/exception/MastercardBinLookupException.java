package com.example.exception;

public class MastercardBinLookupException extends RuntimeException {
    private int statusCode;

    public MastercardBinLookupException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
