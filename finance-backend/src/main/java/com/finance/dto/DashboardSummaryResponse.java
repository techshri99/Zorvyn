package com.finance.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Response object for the dashboard summary endpoint.
 * Contains aggregated financial data for display on the frontend dashboard.
 */
@Data
@Builder
public class DashboardSummaryResponse {

    private BigDecimal totalIncome;       // Sum of all INCOME records
    private BigDecimal totalExpenses;     // Sum of all EXPENSE records
    private BigDecimal netBalance;        // totalIncome - totalExpenses

    private Map<String, BigDecimal> incomeByCategory;   // e.g., {"Salary": 5000, "Freelance": 1000}
    private Map<String, BigDecimal> expenseByCategory;  // e.g., {"Rent": 1200, "Food": 400}

    private List<FinancialRecordResponse> recentTransactions; // Last 10 records

    private Map<Integer, BigDecimal> monthlyIncome;    // e.g., {1: 5000, 2: 4800} (month -> total)
    private Map<Integer, BigDecimal> monthlyExpenses;  // e.g., {1: 2000, 2: 1800}
}
