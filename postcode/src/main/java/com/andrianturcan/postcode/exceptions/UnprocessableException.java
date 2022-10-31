package com.andrianturcan.postcode.exceptions;

public class UnprocessableException extends Exception {
    public UnprocessableException(String errorMessage) {
        super(errorMessage);
    }
}
