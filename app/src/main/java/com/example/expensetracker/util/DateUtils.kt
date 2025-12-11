package com.example.expensetracker.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Utility object for date-related operations
 */
object DateUtils {

    /**
     * Format timestamp to readable date string
     */
    fun formatDate(timestamp: Long, pattern: String = "MMM dd, yyyy"): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }

    /**
     * Format timestamp to display format based on recency
     * Returns "Today", "Yesterday", or formatted date
     */
    fun formatDateRelative(timestamp: Long): String {
        val localDate = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return when (localDate) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> formatDate(timestamp, "MMM dd, yyyy")
        }
    }

    /**
     * Get start of today (midnight)
     */
    fun getStartOfToday(): Long {
        return LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Get end of today (23:59:59.999)
     */
    fun getEndOfToday(): Long {
        return LocalDate.now()
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - 1
    }

    /**
     * Get start of current week (Monday)
     */
    fun getStartOfWeek(): Long {
        val today = LocalDate.now()
        val monday = today.minusDays(today.dayOfWeek.value - 1L)
        return monday.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Get end of current week (Sunday)
     */
    fun getEndOfWeek(): Long {
        val today = LocalDate.now()
        val sunday = today.plusDays(7L - today.dayOfWeek.value)
        return sunday.plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - 1
    }

    /**
     * Get start of current month
     */
    fun getStartOfMonth(): Long {
        val firstDayOfMonth = LocalDate.now().withDayOfMonth(1)
        return firstDayOfMonth.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Get end of current month
     */
    fun getEndOfMonth(): Long {
        val lastDayOfMonth = LocalDate.now()
            .withDayOfMonth(LocalDate.now().lengthOfMonth())
        return lastDayOfMonth.plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - 1
    }

    /**
     * Get current month and year as string (e.g., "January 2024")
     */
    fun getCurrentMonthYear(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        return LocalDate.now().format(formatter)
    }

    /**
     * Group transactions by date
     * Returns a map of date string to list indices
     */
    fun getDaysBetween(startDate: Long, endDate: Long): Long {
        val start = Instant.ofEpochMilli(startDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val end = Instant.ofEpochMilli(endDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return ChronoUnit.DAYS.between(start, end)
    }

    /**
     * Convert LocalDate to timestamp
     */
    fun localDateToTimestamp(localDate: LocalDate): Long {
        return localDate.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Convert timestamp to LocalDate
     */
    fun timestampToLocalDate(timestamp: Long): LocalDate {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}
