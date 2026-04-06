package com.finance.dto;

import com.finance.entity.FinancialRecord;
import com.finance.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response object for financial records returned from the API.
 */
@Data
public class FinancialRecordResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDate date;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByName; // Show the creator's name, not the whole User object

    public static FinancialRecordResponse from(FinancialRecord record) {
        FinancialRecordResponse response = new FinancialRecordResponse();
        response.setId(record.getId());
        response.setAmount(record.getAmount());
        response.setType(record.getType());
        response.setCategory(record.getCategory());
        response.setDate(record.getDate());
        response.setNotes(record.getNotes());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());
        response.setCreatedByName(record.getCreatedBy().getName());
        return response;
    }
}
