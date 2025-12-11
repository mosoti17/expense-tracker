package com.example.expensetracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home Screen
 * Manages UI state and business logic for the home dashboard
 */
class HomeViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    // Get start and end of current month
    private val startOfMonth = DateUtils.getStartOfMonth()
    private val endOfMonth = DateUtils.getEndOfMonth()

    // Recent transactions (last 10)
    val recentTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .map { transactions -> transactions.take(10) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Total income for current month
    val monthlyIncome: StateFlow<Double> = repository.getTotalByTypeAndDateRange(
        type = TransactionType.INCOME,
        startDate = startOfMonth,
        endDate = endOfMonth
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Total expense for current month
    val monthlyExpense: StateFlow<Double> = repository.getTotalByTypeAndDateRange(
        type = TransactionType.EXPENSE,
        startDate = startOfMonth,
        endDate = endOfMonth
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Current balance (income - expense)
    val balance: StateFlow<Double> = combine(
        monthlyIncome,
        monthlyExpense
    ) { income, expense ->
        income - expense
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Highest expense transaction
    val highestExpense: StateFlow<Transaction?> = repository.getHighestExpense()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Most used expense category
    val mostUsedCategory: StateFlow<String?> = repository.getMostUsedCategory(TransactionType.EXPENSE)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * Insert a new transaction
     */
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insert(transaction)
        }
    }

    /**
     * Update an existing transaction
     */
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.update(transaction)
        }
    }

    /**
     * Delete a transaction
     */
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}
