package com.finance.entity;

import com.finance.enums.Role;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a user in the system.
 *
 * @Entity tells JPA this class maps to a database table.
 * @Table(name = "users") sets the table name (avoid using "user" — reserved word in MySQL).
 * Lombok annotations (@Getter, @Setter, etc.) auto-generate boilerplate code.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password; // Stored as bcrypt hash, never plain text

    @Enumerated(EnumType.STRING) // Stores "VIEWER", "ANALYST", or "ADMIN" as a string in DB
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true; // Soft status: admin can deactivate users without deleting
}
