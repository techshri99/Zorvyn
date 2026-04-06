package com.finance.controller;

import com.finance.dto.DashboardSummaryResponse;
import com.finance.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Dashboard summary and analytics endpoints.
 *
 * VIEWER → can see the basic summary
 * ANALYST and ADMIN → same access (all can view the dashboard)
 *
 * Base URL: /api/dashboard
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard/summary
     * Returns the full financial summary for the dashboard.
     *
     * Query param (optional):
     *   ?year=2024   → get monthly trends for a specific year
     *                  defaults to current year if not provided
     *
     * Response includes:
     *   - Total income, expenses, net balance
     *   - Income and expense breakdown by category
     *   - Last 10 recent transactions
     *   - Monthly income and expense totals for the year
     *
     * All authenticated users (VIEWER, ANALYST, ADMIN) can access this.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(dashboardService.getSummary(year));
    }
}
