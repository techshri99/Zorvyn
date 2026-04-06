package com.finance.repository;

import com.finance.entity.User;
import com.finance.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User database operations.
 *
 * JpaRepository<User, Long> gives us free CRUD methods:
 *   save(), findById(), findAll(), deleteById(), etc.
 *
 * We define custom query methods below — Spring auto-generates
 * the SQL from the method names (a feature called "Derived Queries").
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // SELECT * FROM users WHERE role = ?
    List<User> findByRole(Role role);

    // SELECT * FROM users WHERE active = ?
    List<User> findByActive(boolean active);

    // Check if an email is already registered
    boolean existsByEmail(String email);
}
