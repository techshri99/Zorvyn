package com.finance.service;

import com.finance.dto.AuthResponse;
import com.finance.dto.LoginRequest;
import com.finance.entity.User;
import com.finance.repository.UserRepository;
import com.finance.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Handles authentication logic (login).
 *
 * Flow:
 *  1. Client sends email + password
 *  2. AuthenticationManager verifies credentials against the DB
 *  3. If valid, generate a JWT token and return it
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest request) {
        // This throws BadCredentialsException if email/password is wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Credentials are valid — load the full UserDetails for token generation
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        // Fetch the User entity to get name and role for the response
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        return new AuthResponse(token, user.getRole().name(), user.getName());
    }
}
