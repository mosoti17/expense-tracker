package com.example.expensetracker.data.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Category model
 */
class CategoryTest {

    @Test
    fun `getExpenseCategories returns correct count`() {
        val categories = Category.getExpenseCategories()
        assertEquals(8, categories.size)
    }

    @Test
    fun `getIncomeCategories returns correct count`() {
        val categories = Category.getIncomeCategories()
        assertEquals(5, categories.size)
    }

    @Test
    fun `all expense categories have type EXPENSE`() {
        val categories = Category.getExpenseCategories()
        categories.forEach { category ->
            assertEquals(TransactionType.EXPENSE, category.type)
        }
    }

    @Test
    fun `all income categories have type INCOME`() {
        val categories = Category.getIncomeCategories()
        categories.forEach { category ->
            assertEquals(TransactionType.INCOME, category.type)
        }
    }

    @Test
    fun `fromString returns correct category for Food`() {
        val category = Category.fromString("Food & Dining")
        assertEquals(Category.Food, category)
        assertEquals("Food & Dining", category.name)
    }

    @Test
    fun `fromString returns correct category for Salary`() {
        val category = Category.fromString("Salary")
        assertEquals(Category.Salary, category)
        assertEquals("Salary", category.name)
    }

    @Test
    fun `fromString is case sensitive`() {
        val category = Category.fromString("food") // lowercase
        assertEquals(Category.OtherExpense, category) // Should return default
    }

    @Test
    fun `fromString returns OtherExpense for unknown category`() {
        val category = Category.fromString("Unknown Category")
        assertEquals(Category.OtherExpense, category)
    }

    @Test
    fun `all categories have unique names`() {
        val allCategories = Category.getExpenseCategories() + Category.getIncomeCategories()
        val names = allCategories.map { it.name }
        val uniqueNames = names.toSet()
        assertEquals(names.size, uniqueNames.size)
    }

    @Test
    fun `all categories have non-null icons`() {
        val allCategories = Category.getExpenseCategories() + Category.getIncomeCategories()
        allCategories.forEach { category ->
            assertNotNull(category.icon)
        }
    }

    @Test
    fun `all categories have non-null colors`() {
        val allCategories = Category.getExpenseCategories() + Category.getIncomeCategories()
        allCategories.forEach { category ->
            assertNotNull(category.color)
        }
    }

    @Test
    fun `Food category has correct properties`() {
        val category = Category.Food
        assertEquals("Food & Dining", category.name)
        assertEquals(TransactionType.EXPENSE, category.type)
        assertNotNull(category.icon)
        assertNotNull(category.color)
    }

    @Test
    fun `Salary category has correct properties`() {
        val category = Category.Salary
        assertEquals("Salary", category.name)
        assertEquals(TransactionType.INCOME, category.type)
        assertNotNull(category.icon)
        assertNotNull(category.color)
    }
}
