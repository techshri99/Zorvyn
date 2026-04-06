package com.finance.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter.
 *
 * This filter runs ONCE for every HTTP request before it reaches any controller.
 * It checks if the request has a valid JWT token, and if so, sets the
 * authenticated user in Spring Security's context.
 *
 * Flow:
 *   Request → JwtAuthFilter → SecurityContext → Controller
 */
@Component
@RequiredArgsConstructor // Lombok: generates constructor for final fields (dependency injection)
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Read the Authorization header: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        String authHeader = request.getHeader("Authorization");

        // 2. If no valid Bearer token, skip authentication (the request may be a public endpoint)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract just the token part (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // 4. Extract the email from the token
        String email = jwtUtils.extractEmail(token);

        // 5. If email is valid and user is not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Load the user's full details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 7. Validate the token against the loaded user
            if (jwtUtils.validateToken(token, userDetails)) {

                // 8. Create an authentication object and set it in the security context
                //    This tells Spring Security: "this request is authenticated as this user"
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities() // roles/permissions
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Continue to the next filter / controller
        filterChain.doFilter(request, response);
    }
}
