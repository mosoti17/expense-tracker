package com.example.expensetracker.data.repository

import com.example.expensetracker.data.database.TransactionDao
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for TransactionRepository
 */
class TransactionRepositoryTest {

    private lateinit var dao: TransactionDao
    private lateinit var repository: TransactionRepository

    @Before
    fun setup() {
        dao = mock()
        // Set up default mock behavior for allTransactions property
        whenever(dao.getAllTransactions()).thenReturn(flowOf(emptyList()))
        repository = TransactionRepository(dao)
    }

    @Test
    fun `getAllTransactions returns flow from dao`() = runTest {
        val transactions = listOf(
            Transaction(
                id = 1L,
                amount = 100.0,
                category = "Food",
                type = TransactionType.EXPENSE,
                description = "Lunch",
                date = System.currentTimeMillis()
            )
        )

        // Create a fresh mock and repository for this test
        val freshDao: TransactionDao = mock()
        whenever(freshDao.getAllTransactions()).thenReturn(flowOf(transactions))
        val freshRepository = TransactionRepository(freshDao)

        val result = freshRepository.allTransactions.first()

        assertEquals(transactions, result)
        verify(freshDao).getAllTransactions()
    }

    @Test
    fun `insert calls dao insert`() = runTest {
        val transaction = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )

        repository.insert(transaction)

        verify(dao).insert(transaction)
    }

    @Test
    fun `update calls dao update`() = runTest {
        val transaction = Transaction(
            id = 1L,
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )

        repository.update(transaction)

        verify(dao).update(transaction)
    }

    @Test
    fun `delete calls dao delete`() = runTest {
        val transaction = Transaction(
            id = 1L,
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )

        repository.delete(transaction)

        verify(dao).delete(transaction)
    }

    @Test
    fun `getTransactionsByType returns filtered transactions`() = runTest {
        val expenseTransactions = listOf(
            Transaction(
                id = 1L,
                amount = 100.0,
                category = "Food",
                type = TransactionType.EXPENSE,
                description = "Lunch",
                date = System.currentTimeMillis()
            )
        )

        whenever(dao.getTransactionsByType(TransactionType.EXPENSE))
            .thenReturn(flowOf(expenseTransactions))

        val result = repository.getTransactionsByType(TransactionType.EXPENSE).first()

        assertEquals(expenseTransactions, result)
        verify(dao).getTransactionsByType(TransactionType.EXPENSE)
    }

    @Test
    fun `getTransactionsByCategory returns filtered transactions`() = runTest {
        val foodTransactions = listOf(
            Transaction(
                id = 1L,
                amount = 100.0,
                category = "Food",
                type = TransactionType.EXPENSE,
                description = "Lunch",
                date = System.currentTimeMillis()
            )
        )

        whenever(dao.getTransactionsByCategory("Food"))
            .thenReturn(flowOf(foodTransactions))

        val result = repository.getTransactionsByCategory("Food").first()

        assertEquals(foodTransactions, result)
        verify(dao).getTransactionsByCategory("Food")
    }

    @Test
    fun `searchTransactions calls dao search`() = runTest {
        val searchResults = listOf(
            Transaction(
                id = 1L,
                amount = 100.0,
                category = "Food",
                type = TransactionType.EXPENSE,
                description = "Lunch at restaurant",
                date = System.currentTimeMillis()
            )
        )

        whenever(dao.searchTransactions("lunch"))
            .thenReturn(flowOf(searchResults))

        val result = repository.searchTransactions("lunch").first()

        assertEquals(searchResults, result)
        verify(dao).searchTransactions("lunch")
    }
}
