package com.example.expensetracker.util

/**
 * App-wide constants
 */
object Constants {
    const val APP_NAME = "Expense Tracker"
    const val CURRENCY_SYMBOL = "KSh"
    const val CURRENCY_CODE = "KES"
    const val DATABASE_NAME = "expense_database"

    // Date Range Filter Options
    const val FILTER_THIS_WEEK = "This Week"
    const val FILTER_THIS_MONTH = "This Month"
    const val FILTER_CUSTOM = "Custom Range"
    const val FILTER_ALL = "All Time"

    // Default Budget Limit (in KES)
    const val DEFAULT_BUDGET_LIMIT = 500000.0

    // Budget Warning Threshold (percentage)
    const val BUDGET_WARNING_THRESHOLD = 0.8 // 80%
}
