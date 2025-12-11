package com.example.expensetracker.data.model

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

/**
 * Unit tests for Transaction model
 */
class TransactionTest {

    @Test
    fun `Transaction created with default id is 0`() {
        val transaction = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )
        assertEquals(0L, transaction.id)
    }

    @Test
    fun `Transaction created with default createdAt is set`() {
        val beforeCreation = System.currentTimeMillis()
        Thread.sleep(10) // Small delay to ensure time difference

        val transaction = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )

        Thread.sleep(10)
        val afterCreation = System.currentTimeMillis()

        assertTrue(transaction.createdAt >= beforeCreation)
        assertTrue(transaction.createdAt <= afterCreation)
    }

    @Test
    fun `Transaction expense has negative semantic meaning`() {
        val transaction = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )
        assertEquals(TransactionType.EXPENSE, transaction.type)
        assertTrue(transaction.amount > 0) // Amount is stored as positive
    }

    @Test
    fun `Transaction income has positive semantic meaning`() {
        val transaction = Transaction(
            amount = 5000.0,
            category = "Salary",
            type = TransactionType.INCOME,
            description = "Monthly salary",
            date = System.currentTimeMillis()
        )
        assertEquals(TransactionType.INCOME, transaction.type)
        assertTrue(transaction.amount > 0)
    }

    @Test
    fun `Transaction with custom id is preserved`() {
        val transaction = Transaction(
            id = 123L,
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )
        assertEquals(123L, transaction.id)
    }

    @Test
    fun `Transaction with empty description is valid`() {
        val transaction = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "",
            date = System.currentTimeMillis()
        )
        assertEquals("", transaction.description)
    }

    @Test
    fun `Transaction date can be set to past`() {
        val pastDate = LocalDate.of(2025, 1, 1)
        val timestamp = pastDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val transaction = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Past transaction",
            date = timestamp
        )

        assertTrue(transaction.date < System.currentTimeMillis())
    }

    @Test
    fun `Two transactions with same data but different id are not equal`() {
        val transaction1 = Transaction(
            id = 1L,
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = 1000L,
            createdAt = 1000L
        )

        val transaction2 = Transaction(
            id = 2L,
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = 1000L,
            createdAt = 1000L
        )

        assertNotEquals(transaction1, transaction2)
    }

    @Test
    fun `Transaction copy preserves all properties`() {
        val original = Transaction(
            id = 1L,
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = 1000L,
            createdAt = 500L
        )

        val copy = original.copy()

        assertEquals(original.id, copy.id)
        assertEquals(original.amount, copy.amount, 0.01)
        assertEquals(original.category, copy.category)
        assertEquals(original.type, copy.type)
        assertEquals(original.description, copy.description)
        assertEquals(original.date, copy.date)
        assertEquals(original.createdAt, copy.createdAt)
    }

    @Test
    fun `Transaction copy with modified amount works correctly`() {
        val original = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = 1000L
        )

        val modified = original.copy(amount = 200.0)

        assertEquals(200.0, modified.amount, 0.01)
        assertEquals(original.category, modified.category)
        assertEquals(original.type, modified.type)
    }
}
