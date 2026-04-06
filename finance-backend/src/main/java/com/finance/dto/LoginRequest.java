package com.finance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body for POST /api/auth/login
 *
 * @NotBlank ensures the field is not null or empty — returns 400 if violated.
 * @Email ensures the string is a valid email format.
 */
@Data // Lombok: generates getters, setters, toString, equals, hashCode
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
