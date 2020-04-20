package com.myretail.challenge.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidUpdateProductRequest extends RuntimeException {
    public InvalidUpdateProductRequest(String message) {
        super(message);
    }
}
