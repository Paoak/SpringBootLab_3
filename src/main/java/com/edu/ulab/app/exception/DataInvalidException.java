package com.edu.ulab.app.exception;

public class DataInvalidException extends RuntimeException {
    public DataInvalidException(String message) {
        super(message);
    }
}