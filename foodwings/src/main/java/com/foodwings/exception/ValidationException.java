package com.foodwings.exception;

/**
 * Thrown for domain-level validation failures raised programmatically.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
