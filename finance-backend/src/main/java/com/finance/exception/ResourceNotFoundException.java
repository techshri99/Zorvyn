package com.finance.exception;

/**
 * Thrown when a requested resource (user, record) does not exist in the database.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
