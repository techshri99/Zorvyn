package com.finance.dto;

import com.finance.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request body for creating or updating a financial record.
 * Used by: POST /api/records and PUT /api/records/{id}
 */
@Data
public class FinancialRecordRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Type is required (INCOME or EXPENSE)")
    private TransactionType type;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must be under 100 characters")
    private String category;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    @Size(max = 500, message = "Notes must be under 500 characters")
    private String notes; // Optional
}
