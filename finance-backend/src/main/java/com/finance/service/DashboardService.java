package com.finance.service;

import com.finance.dto.DashboardSummaryResponse;
import com.finance.dto.FinancialRecordResponse;
import com.finance.enums.TransactionType;
import com.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides aggregated financial data for the dashboard.
 *
 * This service is the heart of the analytics layer — it takes raw records
 * and computes meaningful summaries for the frontend.
 *
 * VIEWER and above can access the dashboard (enforced in the controller).
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    /**
     * Build the full dashboard summary for the current year.
     *
     * @param year the year to compute monthly trends for (defaults to current year if null)
     */
    public DashboardSummaryResponse getSummary(Integer year) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        // --- Totals ---
        BigDecimal totalIncome = recordRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = recordRepository.sumByType(TransactionType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        // --- Category Breakdown ---
        Map<String, BigDecimal> incomeByCategory = buildCategoryMap(
                recordRepository.sumByCategoryAndType(TransactionType.INCOME));

        Map<String, BigDecimal> expenseByCategory = buildCategoryMap(
                recordRepository.sumByCategoryAndType(TransactionType.EXPENSE));

        // --- Recent Transactions ---
        List<FinancialRecordResponse> recentTransactions = recordRepository
                .findTop10ByOrderByDateDesc()
                .stream()
                .map(FinancialRecordResponse::from)
                .collect(Collectors.toList());

        // --- Monthly Trends ---
        Map<Integer, BigDecimal> monthlyIncome = buildMonthlyMap(
                recordRepository.monthlyTotals(targetYear, TransactionType.INCOME));

        Map<Integer, BigDecimal> monthlyExpenses = buildMonthlyMap(
                recordRepository.monthlyTotals(targetYear, TransactionType.EXPENSE));

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .incomeByCategory(incomeByCategory)
                .expenseByCategory(expenseByCategory)
                .recentTransactions(recentTransactions)
                .monthlyIncome(monthlyIncome)
                .monthlyExpenses(monthlyExpenses)
                .build();
    }

    /**
     * Convert the raw Object[] query results into a readable Map<String, BigDecimal>.
     * Each Object[] row is: [category (String), total (BigDecimal)]
     */
    private Map<String, BigDecimal> buildCategoryMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String category = (String) row[0];
            BigDecimal total = (BigDecimal) row[1];
            map.put(category, total);
        }
        return map;
    }

    /**
     * Convert the raw Object[] query results into a Map<Integer, BigDecimal>.
     * Each Object[] row is: [month (Integer 1-12), total (BigDecimal)]
     */
    private Map<Integer, BigDecimal> buildMonthlyMap(List<Object[]> rows) {
        Map<Integer, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Integer month = ((Number) row[0]).intValue();
            BigDecimal total = (BigDecimal) row[1];
            map.put(month, total);
        }
        return map;
    }
}
