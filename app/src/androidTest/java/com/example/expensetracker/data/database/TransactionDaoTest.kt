package com.example.expensetracker.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented tests for TransactionDao
 * These tests run on an Android device or emulator
 */
@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var transactionDao: TransactionDao
    private lateinit var database: ExpenseDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ExpenseDatabase::class.java
        ).build()
        transactionDao = database.transactionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTransaction() = runTest {
        val transaction = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )

        transactionDao.insert(transaction)
        val allTransactions = transactionDao.getAllTransactions().first()

        assertEquals(1, allTransactions.size)
        assertEquals(transaction.amount, allTransactions[0].amount, 0.01)
        assertEquals(transaction.category, allTransactions[0].category)
    }

    @Test
    @Throws(Exception::class)
    fun getAllTransactionsOrderedByDate() = runTest {
        val transaction1 = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = 1000L
        )

        val transaction2 = Transaction(
            amount = 200.0,
            category = "Transport",
            type = TransactionType.EXPENSE,
            description = "Taxi",
            date = 2000L
        )

        transactionDao.insert(transaction1)
        transactionDao.insert(transaction2)

        val allTransactions = transactionDao.getAllTransactions().first()

        assertEquals(2, allTransactions.size)
        // Should be ordered by date DESC
        assertEquals(2000L, allTransactions[0].date)
        assertEquals(1000L, allTransactions[1].date)
    }

    @Test
    @Throws(Exception::class)
    fun getTransactionsByType() = runTest {
        val expense = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )

        val income = Transaction(
            amount = 5000.0,
            category = "Salary",
            type = TransactionType.INCOME,
            description = "Monthly salary",
            date = System.currentTimeMillis()
        )

        transactionDao.insert(expense)
        transactionDao.insert(income)

        val expenses = transactionDao.getTransactionsByType(TransactionType.EXPENSE).first()
        val incomes = transactionDao.getTransactionsByType(TransactionType.INCOME).first()

        assertEquals(1, expenses.size)
        assertEquals(1, incomes.size)
        assertEquals(TransactionType.EXPENSE, expenses[0].type)
        assertEquals(TransactionType.INCOME, incomes[0].type)
    }

    @Test
    @Throws(Exception::class)
    fun getTransactionsByCategory() = runTest {
        val food1 = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )

        val food2 = Transaction(
            amount = 150.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Dinner",
            date = System.currentTimeMillis()
        )

        val transport = Transaction(
            amount = 50.0,
            category = "Transport",
            type = TransactionType.EXPENSE,
            description = "Bus",
            date = System.currentTimeMillis()
        )

        transactionDao.insert(food1)
        transactionDao.insert(food2)
        transactionDao.insert(transport)

        val foodTransactions = transactionDao.getTransactionsByCategory("Food").first()

        assertEquals(2, foodTransactions.size)
        assertTrue(foodTransactions.all { it.category == "Food" })
    }

    @Test
    @Throws(Exception::class)
    fun searchTransactions() = runTest {
        val transaction1 = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch at restaurant",
            date = System.currentTimeMillis()
        )

        val transaction2 = Transaction(
            amount = 50.0,
            category = "Transport",
            type = TransactionType.EXPENSE,
            description = "Taxi ride",
            date = System.currentTimeMillis()
        )

        transactionDao.insert(transaction1)
        transactionDao.insert(transaction2)

        val searchResults = transactionDao.searchTransactions("%restaurant%").first()

        assertEquals(1, searchResults.size)
        assertTrue(searchResults[0].description.contains("restaurant"))
    }

    @Test
    @Throws(Exception::class)
    fun updateTransaction() = runTest {
        val transaction = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )

        transactionDao.insert(transaction)
        val inserted = transactionDao.getAllTransactions().first()[0]

        val updated = inserted.copy(amount = 200.0, description = "Dinner")
        transactionDao.update(updated)

        val result = transactionDao.getAllTransactions().first()[0]

        assertEquals(200.0, result.amount, 0.01)
        assertEquals("Dinner", result.description)
        assertEquals(inserted.id, result.id)
    }

    @Test
    @Throws(Exception::class)
    fun deleteTransaction() = runTest {
        val transaction = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )

        transactionDao.insert(transaction)
        var allTransactions = transactionDao.getAllTransactions().first()
        assertEquals(1, allTransactions.size)

        transactionDao.delete(allTransactions[0])
        allTransactions = transactionDao.getAllTransactions().first()

        assertEquals(0, allTransactions.size)
    }

    @Test
    @Throws(Exception::class)
    fun getTotalByType() = runTest {
        val expense1 = Transaction(
            amount = 100.0,
            category = "Food",
            type = TransactionType.EXPENSE,
            description = "Lunch",
            date = System.currentTimeMillis()
        )

        val expense2 = Transaction(
            amount = 50.0,
            category = "Transport",
            type = TransactionType.EXPENSE,
            description = "Bus",
            date = System.currentTimeMillis()
        )

        transactionDao.insert(expense1)
        transactionDao.insert(expense2)

        val total = transactionDao.getTotalByType(TransactionType.EXPENSE).first()

        assertEquals(150.0, total ?: 0.0, 0.01)
    }
}
