package com.finance.controller;

import com.finance.dto.UserRequest;
import com.finance.dto.UserResponse;
import com.finance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User management endpoints.
 *
 * ALL endpoints here are restricted to ADMIN role only.
 * @PreAuthorize("hasRole('ADMIN')") is evaluated BEFORE the method runs.
 * If the token belongs to a VIEWER or ANALYST, Spring returns 403 Forbidden.
 *
 * Base URL: /api/users
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Applied to ALL methods in this controller
public class UserController {

    private final UserService userService;

    /**
     * POST /api/users
     * Create a new user. Returns 201 Created with the new user's data.
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    /**
     * GET /api/users
     * Get all users in the system.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * GET /api/users/{id}
     * Get a single user by their ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * PUT /api/users/{id}
     * Update a user's name, email, password, or role.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                    @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * PATCH /api/users/{id}/toggle-status
     * Activate or deactivate a user without deleting them.
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<UserResponse> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }

    /**
     * DELETE /api/users/{id}
     * Permanently delete a user.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
