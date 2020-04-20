package com.myretail.challenge.service;

public class ResponseParseException extends RuntimeException {
    public ResponseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
