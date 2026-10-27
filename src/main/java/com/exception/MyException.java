package com.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MyException extends RuntimeException {
    private final HttpStatus statusCode;

    public MyException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

}