package com.finance.exception;

/**
 * Thrown when the request is syntactically valid but violates a business rule.
 * Example: trying to register with an email that's already taken.
 * Maps to HTTP 400 Bad Request.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
