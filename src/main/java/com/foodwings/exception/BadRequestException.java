package com.foodwings.exception;

/**
 * Thrown for invalid client requests that are not validation-annotation failures.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
