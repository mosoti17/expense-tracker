package com.example.expensetracker.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * Utility object for currency formatting and operations
 * Localized for Kenya (KES)
 */
object CurrencyUtils {

    // Use Kenyan locale for number formatting
    private val kenyaLocale = Locale("en", "KE")
    private val decimalFormat = DecimalFormat("#,##0.00")

    /**
     * Format amount as currency with KES symbol
     * Example: 1234.56 -> "KSh 1,234.56"
     */
    fun formatCurrency(amount: Double): String {
        val formatted = decimalFormat.format(amount)
        return "${Constants.CURRENCY_SYMBOL} $formatted"
    }

    /**
     * Format amount with currency symbol and +/- prefix
     * Example: 1234.56 (income) -> "+KSh 1,234.56"
     *          1234.56 (expense) -> "-KSh 1,234.56"
     */
    fun formatCurrencyWithSign(amount: Double, isIncome: Boolean): String {
        val formatted = formatCurrency(amount)
        return if (isIncome) "+$formatted" else "-$formatted"
    }

    /**
     * Format amount as simple decimal
     * Example: 1234.56 -> "1,234.56"
     */
    fun formatAmount(amount: Double): String {
        return decimalFormat.format(amount)
    }

    /**
     * Parse currency string to double
     * Example: "KSh 1,234.56" -> 1234.56
     */
    fun parseCurrency(currencyString: String): Double? {
        return try {
            val cleaned = currencyString.replace(Regex("[^0-9.]"), "")
            cleaned.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get currency symbol
     */
    fun getCurrencySymbol(): String {
        return Constants.CURRENCY_SYMBOL
    }

    /**
     * Format as compact currency for large amounts
     * Example: 1234567.89 -> "KSh 1.23M"
     */
    fun formatCompactCurrency(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format("${Constants.CURRENCY_SYMBOL} %.2fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("${Constants.CURRENCY_SYMBOL} %.2fK", amount / 1_000)
            else -> formatCurrency(amount)
        }
    }

    /**
     * Calculate percentage
     */
    fun calculatePercentage(part: Double, total: Double): Double {
        return if (total == 0.0) 0.0 else (part / total) * 100
    }

    /**
     * Format percentage
     */
    fun formatPercentage(percentage: Double): String {
        return String.format(kenyaLocale, "%.2f%%", percentage)
    }
}
