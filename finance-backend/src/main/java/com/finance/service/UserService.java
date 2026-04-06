package com.finance.service;

import com.finance.dto.UserRequest;
import com.finance.dto.UserResponse;
import com.finance.entity.User;
import com.finance.exception.BadRequestException;
import com.finance.exception.ResourceNotFoundException;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for user management.
 * Only ADMINs can access these operations (enforced in the controller via @PreAuthorize).
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new user.
     * Rejects duplicate emails to prevent data integrity issues.
     */
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Always hash passwords
                .role(request.getRole())
                .active(true)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Get all users in the system.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get a single user by ID.
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponse.from(user);
    }

    /**
     * Update user details and/or role.
     * If a new password is provided, it will be re-hashed.
     */
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check email uniqueness if the email is being changed
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Toggle user active/inactive status.
     * This is a "soft disable" — the user record is kept but access is blocked.
     */
    public UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(!user.isActive()); // flip the flag
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Permanently delete a user.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
