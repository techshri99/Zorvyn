package com.finance.repository;

import com.finance.entity.FinancialRecord;
import com.finance.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for FinancialRecord database operations.
 *
 * Mix of Derived Queries (method names) and @Query (custom JPQL).
 * JPQL = Java Persistence Query Language — like SQL but uses entity class names.
 */
@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // -------------------------
    // FILTERING QUERIES
    // -------------------------

    // Filter by transaction type (INCOME or EXPENSE)
    List<FinancialRecord> findByType(TransactionType type);

    // Filter by category (e.g., "Rent", "Salary")
    List<FinancialRecord> findByCategory(String category);

    // Filter records within a date range
    List<FinancialRecord> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Filter by both type and date range
    List<FinancialRecord> findByTypeAndDateBetween(TransactionType type, LocalDate start, LocalDate end);

    // Filter by category and date range
    List<FinancialRecord> findByCategoryAndDateBetween(String category, LocalDate start, LocalDate end);

    // Get the most recent N records (for "Recent Activity" on the dashboard)
    List<FinancialRecord> findTop10ByOrderByDateDesc();

    // -------------------------
    // AGGREGATION QUERIES (for Dashboard)
    // -------------------------

    // SUM of all amounts for a given type (e.g., total INCOME)
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.type = :type")
    BigDecimal sumByType(@Param("type") TransactionType type);

    // SUM grouped by category — returns list of [category, total] pairs
    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.type = :type GROUP BY r.category")
    List<Object[]> sumByCategoryAndType(@Param("type") TransactionType type);

    // Monthly totals for a given year and type — returns [month, total] pairs
    @Query("SELECT MONTH(r.date), SUM(r.amount) FROM FinancialRecord r " +
           "WHERE YEAR(r.date) = :year AND r.type = :type GROUP BY MONTH(r.date) ORDER BY MONTH(r.date)")
    List<Object[]> monthlyTotals(@Param("year") int year, @Param("type") TransactionType type);
}
