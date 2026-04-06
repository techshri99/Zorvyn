package com.finance;

import com.finance.entity.User;
import com.finance.enums.Role;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application entry point.
 *
 * The seedAdminUser() method runs once on startup.
 * It checks if any admin exists — if not, it creates a default admin
 * so you have a way to log in for the first time.
 *
 * Default credentials:
 *   Email:    admin@finance.com
 *   Password: admin123
 *
 * IMPORTANT: Change these in production!
 */
@SpringBootApplication
@RequiredArgsConstructor
public class FinanceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceBackendApplication.class, args);
    }

    /**
     * Seed a default admin user on first startup.
     * CommandLineRunner runs after the application context is loaded.
     */
    @Bean
    CommandLineRunner seedAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@finance.com")) {
                User admin = User.builder()
                        .name("System Admin")
                        .email("admin@finance.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .active(true)
                        .build();
                userRepository.save(admin);
                System.out.println("===================================================");
                System.out.println("  Default admin created: admin@finance.com / admin123");
                System.out.println("===================================================");
            }
        };
    }
}
