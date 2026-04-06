package com.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response body returned after a successful login.
 * Contains the JWT token the client will use for all future requests.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
    private String name;
}
