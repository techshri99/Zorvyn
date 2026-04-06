package com.finance.dto;

import com.finance.entity.User;
import com.finance.enums.Role;
import lombok.Data;

/**
 * Response object for user data.
 * NEVER include the password field in API responses — this DTO ensures that.
 */
@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean active;

    /**
     * Static factory method — converts a User entity to a UserResponse DTO.
     * Calling UserResponse.from(user) is cleaner than doing it manually every time.
     */
    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        return response;
    }
}
