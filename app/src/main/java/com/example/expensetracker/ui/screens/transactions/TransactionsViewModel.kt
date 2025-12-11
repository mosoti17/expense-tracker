package com.example.expensetracker.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI State for TransactionsScreen
 */
data class TransactionsUiState(
    val searchQuery: String = "",
    val selectedType: TransactionType? = null,
    val selectedCategory: String? = null,
    val dateFilterType: DateFilterType = DateFilterType.ALL,
    val customStartDate: Long? = null,
    val customEndDate: Long? = null
)

/**
 * Date filter options
 */
enum class DateFilterType {
    ALL,
    THIS_WEEK,
    THIS_MONTH,
    CUSTOM
}

/**
 * ViewModel for the Transactions Screen
 * Manages filtering, searching, and transaction operations
 */
class TransactionsViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    // Get filtered transactions based on current UI state
    val transactions: StateFlow<List<Transaction>> = combine(
        repository.allTransactions,
        _uiState
    ) { allTransactions, uiState ->
        var filtered = allTransactions

        // Apply type filter
        if (uiState.selectedType != null) {
            filtered = filtered.filter { it.type == uiState.selectedType }
        }

        // Apply category filter
        if (uiState.selectedCategory != null) {
            filtered = filtered.filter { it.category == uiState.selectedCategory }
        }

        // Apply date filter
        filtered = when (uiState.dateFilterType) {
            DateFilterType.THIS_WEEK -> {
                val start = DateUtils.getStartOfWeek()
                val end = DateUtils.getEndOfWeek()
                filtered.filter { it.date in start..end }
            }
            DateFilterType.THIS_MONTH -> {
                val start = DateUtils.getStartOfMonth()
                val end = DateUtils.getEndOfMonth()
                filtered.filter { it.date in start..end }
            }
            DateFilterType.CUSTOM -> {
                if (uiState.customStartDate != null && uiState.customEndDate != null) {
                    filtered.filter { it.date in uiState.customStartDate..uiState.customEndDate }
                } else {
                    filtered
                }
            }
            DateFilterType.ALL -> filtered
        }

        // Apply search query
        if (uiState.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.description.contains(uiState.searchQuery, ignoreCase = true) ||
                        it.category.contains(uiState.searchQuery, ignoreCase = true)
            }
        }

        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Update search query
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Set type filter
     */
    fun setTypeFilter(type: TransactionType?) {
        _uiState.update { it.copy(selectedType = type) }
    }

    /**
     * Set category filter
     */
    fun setCategoryFilter(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    /**
     * Set date filter
     */
    fun setDateFilter(filterType: DateFilterType, startDate: Long? = null, endDate: Long? = null) {
        _uiState.update {
            it.copy(
                dateFilterType = filterType,
                customStartDate = startDate,
                customEndDate = endDate
            )
        }
    }

    /**
     * Clear all filters
     */
    fun clearFilters() {
        _uiState.value = TransactionsUiState()
    }

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
