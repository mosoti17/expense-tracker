package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.data.database.ExpenseDatabase
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.ui.navigation.AppNavigation
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.util.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Main Activity - Entry point for the Expense Tracker app
 */
class MainActivity : ComponentActivity() {

    private lateinit var repository: TransactionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize database and repository
        val database = ExpenseDatabase.getDatabase(applicationContext)
        repository = TransactionRepository(database.transactionDao())

        // Initialize sample data if database is empty
        initializeSampleData()

        setContent {
            ExpenseTrackerTheme {
                AppNavigation(repository = repository)
            }
        }
    }

    /**
     * Initialize sample data for demonstration
     * Only adds data if the database is empty
     */
    private fun initializeSampleData() {
        lifecycleScope.launch {
            val count = repository.getTransactionCount().first()
            if (count == 0) {
                val sampleTransactions = generateSampleTransactions()
                repository.insertAll(sampleTransactions)
            }
        }
    }

    /**
     * Generate sample transactions for demonstration
     */
    private fun generateSampleTransactions(): List<Transaction> {
        val today = LocalDate.now()
        val transactions = mutableListOf<Transaction>()

        // Current month transactions (amounts in KES - realistic Kenyan amounts)
        // Income transactions
        transactions.add(
            Transaction(
                amount = 850000.0,
                category = Category.Salary.name,
                type = TransactionType.INCOME,
                description = "Monthly salary",
                date = DateUtils.localDateToTimestamp(today.minusDays(25))
            )
        )

        transactions.add(
            Transaction(
                amount = 150000.0,
                category = Category.Freelance.name,
                type = TransactionType.INCOME,
                description = "Website project",
                date = DateUtils.localDateToTimestamp(today.minusDays(15))
            )
        )

        transactions.add(
            Transaction(
                amount = 85000.0,
                category = Category.Investments.name,
                type = TransactionType.INCOME,
                description = "Stock dividends",
                date = DateUtils.localDateToTimestamp(today.minusDays(10))
            )
        )

        // Expense transactions
        transactions.add(
            Transaction(
                amount = 250000.0,
                category = Category.Bills.name,
                type = TransactionType.EXPENSE,
                description = "Rent payment",
                date = DateUtils.localDateToTimestamp(today.minusDays(28))
            )
        )

        transactions.add(
            Transaction(
                amount = 45000.0,
                category = Category.Food.name,
                type = TransactionType.EXPENSE,
                description = "Grocery shopping at Naivas",
                date = DateUtils.localDateToTimestamp(today.minusDays(2))
            )
        )

        transactions.add(
            Transaction(
                amount = 32000.0,
                category = Category.Transport.name,
                type = TransactionType.EXPENSE,
                description = "Petrol",
                date = DateUtils.localDateToTimestamp(today.minusDays(3))
            )
        )

        transactions.add(
            Transaction(
                amount = 68000.0,
                category = Category.Shopping.name,
                type = TransactionType.EXPENSE,
                description = "New shoes at Bata",
                date = DateUtils.localDateToTimestamp(today.minusDays(5))
            )
        )

        transactions.add(
            Transaction(
                amount = 55000.0,
                category = Category.Bills.name,
                type = TransactionType.EXPENSE,
                description = "KPLC - Electricity bill",
                date = DateUtils.localDateToTimestamp(today.minusDays(7))
            )
        )

        transactions.add(
            Transaction(
                amount = 28000.0,
                category = Category.Entertainment.name,
                type = TransactionType.EXPENSE,
                description = "Movies at Westgate Mall",
                date = DateUtils.localDateToTimestamp(today.minusDays(8))
            )
        )

        transactions.add(
            Transaction(
                amount = 35000.0,
                category = Category.Healthcare.name,
                type = TransactionType.EXPENSE,
                description = "Doctor consultation",
                date = DateUtils.localDateToTimestamp(today.minusDays(12))
            )
        )

        transactions.add(
            Transaction(
                amount = 18000.0,
                category = Category.Food.name,
                type = TransactionType.EXPENSE,
                description = "Lunch at Java House",
                date = DateUtils.localDateToTimestamp(today.minusDays(4))
            )
        )

        transactions.add(
            Transaction(
                amount = 25000.0,
                category = Category.Transport.name,
                type = TransactionType.EXPENSE,
                description = "Uber rides",
                date = DateUtils.localDateToTimestamp(today.minusDays(6))
            )
        )

        transactions.add(
            Transaction(
                amount = 12000.0,
                category = Category.Entertainment.name,
                type = TransactionType.EXPENSE,
                description = "Netflix subscription",
                date = DateUtils.localDateToTimestamp(today.minusDays(14))
            )
        )

        transactions.add(
            Transaction(
                amount = 9500.0,
                category = Category.Food.name,
                type = TransactionType.EXPENSE,
                description = "Coffee at Artcaffe",
                date = DateUtils.localDateToTimestamp(today.minusDays(1))
            )
        )

        transactions.add(
            Transaction(
                amount = 120000.0,
                category = Category.Education.name,
                type = TransactionType.EXPENSE,
                description = "Online course - Udemy",
                date = DateUtils.localDateToTimestamp(today.minusDays(9))
            )
        )

        transactions.add(
            Transaction(
                amount = 15000.0,
                category = Category.Bills.name,
                type = TransactionType.EXPENSE,
                description = "Safaricom - Mobile & Internet",
                date = DateUtils.localDateToTimestamp(today.minusDays(11))
            )
        )

        // Previous month transactions
        transactions.add(
            Transaction(
                amount = 850000.0,
                category = Category.Salary.name,
                type = TransactionType.INCOME,
                description = "Monthly salary",
                date = DateUtils.localDateToTimestamp(today.minusMonths(1).withDayOfMonth(1))
            )
        )

        transactions.add(
            Transaction(
                amount = 250000.0,
                category = Category.Bills.name,
                type = TransactionType.EXPENSE,
                description = "Rent payment",
                date = DateUtils.localDateToTimestamp(today.minusMonths(1).withDayOfMonth(3))
            )
        )

        transactions.add(
            Transaction(
                amount = 85000.0,
                category = Category.Food.name,
                type = TransactionType.EXPENSE,
                description = "Groceries at Carrefour",
                date = DateUtils.localDateToTimestamp(today.minusMonths(1).withDayOfMonth(10))
            )
        )

        return transactions
    }
}
