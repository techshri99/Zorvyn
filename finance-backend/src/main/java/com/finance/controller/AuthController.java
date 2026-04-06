package com.finance.controller;

import com.finance.dto.AuthResponse;
import com.finance.dto.LoginRequest;
import com.finance.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public endpoints for authentication.
 * No JWT token required for these — they are whitelisted in SecurityConfig.
 *
 * POST /api/auth/login  → returns JWT token on valid credentials
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint.
     *
     * Request:  { "email": "admin@example.com", "password": "secret123" }
     * Response: { "token": "eyJ...", "role": "ADMIN", "name": "Alice" }
     *
     * @Valid triggers validation on LoginRequest fields before the method runs.
     * If validation fails, GlobalExceptionHandler returns a 400 with details.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
