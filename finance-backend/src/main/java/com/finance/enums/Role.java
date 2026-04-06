package com.finance.enums;

/**
 * Defines the three access levels in the system.
 *
 * VIEWER  → Can only read dashboard data and financial records
 * ANALYST → Can read records AND access detailed summaries/insights
 * ADMIN   → Full access: create, update, delete records and manage users
 */
public enum Role {
    VIEWER,
    ANALYST,
    ADMIN
}
