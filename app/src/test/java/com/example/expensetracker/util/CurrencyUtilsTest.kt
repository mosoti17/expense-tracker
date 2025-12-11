package com.example.expensetracker.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for CurrencyUtils
 */
class CurrencyUtilsTest {

    @Test
    fun `formatCurrency formats positive amount correctly`() {
        val amount = 1234.56
        val result = CurrencyUtils.formatCurrency(amount)
        assertEquals("KSh 1,234.56", result)
    }

    @Test
    fun `formatCurrency formats zero correctly`() {
        val amount = 0.0
        val result = CurrencyUtils.formatCurrency(amount)
        assertEquals("KSh 0.00", result)
    }

    @Test
    fun `formatCurrency formats negative amount correctly`() {
        val amount = -500.75
        val result = CurrencyUtils.formatCurrency(amount)
        assertEquals("KSh -500.75", result)
    }

    @Test
    fun `formatCurrency formats large amount correctly`() {
        val amount = 1000000.99
        val result = CurrencyUtils.formatCurrency(amount)
        assertEquals("KSh 1,000,000.99", result)
    }

    @Test
    fun `formatCurrency formats amount with single decimal`() {
        val amount = 100.5
        val result = CurrencyUtils.formatCurrency(amount)
        assertEquals("KSh 100.50", result)
    }

    @Test
    fun `formatCurrency formats whole number correctly`() {
        val amount = 500.0
        val result = CurrencyUtils.formatCurrency(amount)
        assertEquals("KSh 500.00", result)
    }

    @Test
    fun `formatPercentage formats correctly`() {
        val percentage = 45.67
        val result = CurrencyUtils.formatPercentage(percentage)
        assertEquals("45.67%", result)
    }

    @Test
    fun `formatPercentage formats zero correctly`() {
        val percentage = 0.0
        val result = CurrencyUtils.formatPercentage(percentage)
        assertEquals("0.00%", result)
    }

    @Test
    fun `formatPercentage formats 100 percent correctly`() {
        val percentage = 100.0
        val result = CurrencyUtils.formatPercentage(percentage)
        assertEquals("100.00%", result)
    }
}
