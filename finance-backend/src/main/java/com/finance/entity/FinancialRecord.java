package com.finance.entity;

import com.finance.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single financial entry (income or expense).
 *
 * BigDecimal is used for amount instead of double/float
 * because it avoids floating-point precision errors — critical for financial data.
 */
@Entity
@Table(name = "financial_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount; // e.g., 12345.67

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // INCOME or EXPENSE

    @Column(nullable = false, length = 100)
    private String category; // e.g., "Salary", "Rent", "Food"

    @Column(nullable = false)
    private LocalDate date; // The date of the transaction

    @Column(length = 500)
    private String notes; // Optional description

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // Auto-set on creation

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now(); // Updated on every save

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // Which user created this record

    // Automatically update the updatedAt timestamp before every DB update
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
