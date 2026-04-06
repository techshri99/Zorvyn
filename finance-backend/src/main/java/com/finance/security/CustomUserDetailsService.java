package com.finance.security;

import com.finance.entity.User;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security calls this service to load user details during authentication.
 *
 * We tell it: "look up the user by email in our database,
 * and wrap them in a Spring Security UserDetails object."
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Called by Spring Security with the "username" (which in our app is the email).
     * Returns a UserDetails object that Spring Security uses to verify the password
     * and check authorities (roles).
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Check if user is active — inactive users cannot log in
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + email);
        }

        // Convert our Role enum to a Spring Security authority: "ROLE_ADMIN", "ROLE_VIEWER", etc.
        // Spring Security's @PreAuthorize("hasRole('ADMIN')") checks for this prefix.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
