package com.finance.service;

import com.finance.dto.FinancialRecordRequest;
import com.finance.dto.FinancialRecordResponse;
import com.finance.entity.FinancialRecord;
import com.finance.entity.User;
import com.finance.enums.TransactionType;
import com.finance.exception.ResourceNotFoundException;
import com.finance.repository.FinancialRecordRepository;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for financial record management.
 *
 * ANALYST and ADMIN can create/update/delete records (enforced in the controller).
 * All authenticated users can read records.
 */
@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    /**
     * Helper: get the currently logged-in user from the security context.
     * Spring Security stores the authenticated user here after JWT validation.
     */
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    /**
     * Create a new financial record.
     * The currently logged-in user is automatically set as the creator.
     */
    public FinancialRecordResponse createRecord(FinancialRecordRequest request) {
        User currentUser = getCurrentUser();

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .createdBy(currentUser)
                .build();

        return FinancialRecordResponse.from(recordRepository.save(record));
    }

    /**
     * Get all records, with optional filters.
     * All parameters are optional — if null, the filter is not applied.
     *
     * @param type      filter by INCOME or EXPENSE
     * @param category  filter by category string
     * @param startDate filter by date range start (inclusive)
     * @param endDate   filter by date range end (inclusive)
     */
    public List<FinancialRecordResponse> getRecords(
            TransactionType type,
            String category,
            LocalDate startDate,
            LocalDate endDate) {

        List<FinancialRecord> records;

        // Apply the most specific filter combination available
        if (type != null && startDate != null && endDate != null) {
            records = recordRepository.findByTypeAndDateBetween(type, startDate, endDate);
        } else if (category != null && startDate != null && endDate != null) {
            records = recordRepository.findByCategoryAndDateBetween(category, startDate, endDate);
        } else if (type != null) {
            records = recordRepository.findByType(type);
        } else if (category != null) {
            records = recordRepository.findByCategory(category);
        } else if (startDate != null && endDate != null) {
            records = recordRepository.findByDateBetween(startDate, endDate);
        } else {
            records = recordRepository.findAll(); // No filter — return everything
        }

        return records.stream()
                .map(FinancialRecordResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get a single record by ID.
     */
    public FinancialRecordResponse getRecordById(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
        return FinancialRecordResponse.from(record);
    }

    /**
     * Update an existing record.
     */
    public FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest request) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());
        // updatedAt is set automatically via @PreUpdate in the entity

        return FinancialRecordResponse.from(recordRepository.save(record));
    }

    /**
     * Delete a record permanently.
     */
    public void deleteRecord(Long id) {
        if (!recordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Record not found with id: " + id);
        }
        recordRepository.deleteById(id);
    }
}
