package com.finance.controller;

import com.finance.dto.FinancialRecordRequest;
import com.finance.dto.FinancialRecordResponse;
import com.finance.enums.TransactionType;
import com.finance.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Financial record management endpoints.
 *
 * READ operations → all authenticated users (VIEWER, ANALYST, ADMIN)
 * WRITE operations → ANALYST and ADMIN only
 * DELETE operations → ADMIN only
 *
 * Base URL: /api/records
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    /**
     * POST /api/records
     * Create a new financial record.
     * Allowed: ANALYST, ADMIN
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<FinancialRecordResponse> createRecord(
            @Valid @RequestBody FinancialRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordService.createRecord(request));
    }

    /**
     * GET /api/records
     * Retrieve records with optional query filters.
     * All authenticated users can access this.
     *
     * Query params (all optional):
     *   ?type=INCOME
     *   ?category=Rent
     *   ?startDate=2024-01-01&endDate=2024-12-31
     *
     * Example: GET /api/records?type=EXPENSE&startDate=2024-06-01&endDate=2024-06-30
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<List<FinancialRecordResponse>> getRecords(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(recordService.getRecords(type, category, startDate, endDate));
    }

    /**
     * GET /api/records/{id}
     * Get a single record by ID.
     * All authenticated users can access this.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<FinancialRecordResponse> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(recordService.getRecordById(id));
    }

    /**
     * PUT /api/records/{id}
     * Update an existing record.
     * Allowed: ANALYST, ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<FinancialRecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordRequest request) {
        return ResponseEntity.ok(recordService.updateRecord(id, request));
    }

    /**
     * DELETE /api/records/{id}
     * Delete a record permanently.
     * Allowed: ADMIN only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
