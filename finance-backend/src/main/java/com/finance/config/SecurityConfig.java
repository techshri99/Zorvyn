package com.finance.config;

import com.finance.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration class.
 *
 * Key decisions made here:
 * - Which endpoints are public (no token needed) vs protected
 * - Stateless session (we use JWT, not server-side sessions)
 * - Password encoding (BCrypt)
 * - Where to plug in our JWT filter
 *
 * @EnableMethodSecurity enables @PreAuthorize annotations on controller methods
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Defines the security filter chain — the rules for HTTP request handling.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — not needed for stateless REST APIs using JWT
            .csrf(csrf -> csrf.disable())

            // Define which URLs are public and which need authentication
            .authorizeHttpRequests(auth -> auth
                // Public endpoints: login and first-time admin registration
                .requestMatchers("/api/auth/**").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )

            // Use stateless sessions — each request must carry its own JWT token
            // The server does NOT store any session state
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Use our custom authentication provider
            .authenticationProvider(authenticationProvider())

            // Add our JWT filter BEFORE Spring's default username/password filter
            // This ensures JWT tokens are validated on every request
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures how authentication is performed:
     * - Load user from DB using our service
     * - Verify password using BCrypt
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * BCrypt password encoder.
     * BCrypt is a one-way hashing algorithm — passwords are never stored in plain text.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean — used in AuthService for login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
