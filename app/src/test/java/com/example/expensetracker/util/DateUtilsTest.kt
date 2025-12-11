package com.example.expensetracker.util

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

/**
 * Unit tests for DateUtils
 */
class DateUtilsTest {

    @Test
    fun `formatDateRelative returns Today for today's date`() {
        val today = LocalDate.now()
        val timestamp = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val result = DateUtils.formatDateRelative(timestamp)
        assertEquals("Today", result)
    }

    @Test
    fun `formatDateRelative returns Yesterday for yesterday's date`() {
        val yesterday = LocalDate.now().minusDays(1)
        val timestamp = yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val result = DateUtils.formatDateRelative(timestamp)
        assertEquals("Yesterday", result)
    }

    @Test
    fun `getStartOfMonth returns timestamp for first day of current month`() {
        val result = DateUtils.getStartOfMonth()
        val localDate = DateUtils.timestampToLocalDate(result)
        assertEquals(1, localDate.dayOfMonth)
        assertEquals(LocalDate.now().month, localDate.month)
    }

    @Test
    fun `getEndOfMonth returns timestamp for last day of current month`() {
        val result = DateUtils.getEndOfMonth()
        val localDate = DateUtils.timestampToLocalDate(result)
        val lastDay = LocalDate.now().lengthOfMonth()
        assertEquals(lastDay, localDate.dayOfMonth)
    }

    @Test
    fun `getCurrentMonthYear returns correct format`() {
        val result = DateUtils.getCurrentMonthYear()
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        // Format should be like "December 2025"
        assertTrue(result.contains(" "))
    }

    @Test
    fun `getStartOfWeek returns Monday of current week`() {
        val result = DateUtils.getStartOfWeek()
        val localDate = DateUtils.timestampToLocalDate(result)
        val dayOfWeek = localDate.dayOfWeek.value
        assertEquals(1, dayOfWeek) // Monday is 1
    }

    @Test
    fun `getStartOfToday returns timestamp for start of today`() {
        val result = DateUtils.getStartOfToday()
        val localDate = DateUtils.timestampToLocalDate(result)
        assertEquals(LocalDate.now(), localDate)
    }

    @Test
    fun `getDaysBetween calculates days correctly`() {
        val date1 = LocalDate.of(2025, 12, 1)
        val date2 = LocalDate.of(2025, 12, 10)
        val timestamp1 = DateUtils.localDateToTimestamp(date1)
        val timestamp2 = DateUtils.localDateToTimestamp(date2)
        val result = DateUtils.getDaysBetween(timestamp1, timestamp2)
        assertEquals(9L, result)
    }

    @Test
    fun `timestampToLocalDate converts correctly`() {
        val date = LocalDate.of(2025, 12, 1)
        val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val result = DateUtils.timestampToLocalDate(timestamp)
        assertEquals(date, result)
    }

    @Test
    fun `localDateToTimestamp converts correctly`() {
        val date = LocalDate.of(2025, 12, 1)
        val timestamp = DateUtils.localDateToTimestamp(date)
        val backToDate = DateUtils.timestampToLocalDate(timestamp)
        assertEquals(date, backToDate)
    }
}
