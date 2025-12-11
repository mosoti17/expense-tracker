package com.example.expensetracker.ui.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.util.Constants
import com.example.expensetracker.util.DateUtils
import kotlinx.coroutines.flow.*

/**
 * ViewModel for the Budget Screen
 * Manages budget limit and spending tracking
 */
class BudgetViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    // Budget limit (can be persisted in preferences or database in future)
    private val _budgetLimit = MutableStateFlow(Constants.DEFAULT_BUDGET_LIMIT)
    val budgetLimit: StateFlow<Double> = _budgetLimit.asStateFlow()

    // Get start and end of current month
    private val startOfMonth = DateUtils.getStartOfMonth()
    private val endOfMonth = DateUtils.getEndOfMonth()

    // Current month's spending
    val monthlySpending: StateFlow<Double> = repository.getTotalByTypeAndDateRange(
        type = TransactionType.EXPENSE,
        startDate = startOfMonth,
        endDate = endOfMonth
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Remaining budget
    val remainingBudget: StateFlow<Double> = combine(
        budgetLimit,
        monthlySpending
    ) { limit, spending ->
        limit - spending
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Constants.DEFAULT_BUDGET_LIMIT
    )

    // Budget progress (0.0 to 1.0)
    val budgetProgress: StateFlow<Float> = combine(
        budgetLimit,
        monthlySpending
    ) { limit, spending ->
        if (limit > 0) (spending / limit).toFloat().coerceIn(0f, 1f)
        else 0f
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    // Is over budget
    val isOverBudget: StateFlow<Boolean> = combine(
        budgetLimit,
        monthlySpending
    ) { limit, spending ->
        spending > limit
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Is approaching budget (80% or more)
    val isApproachingBudget: StateFlow<Boolean> = combine(
        budgetLimit,
        monthlySpending
    ) { limit, spending ->
        spending >= (limit * Constants.BUDGET_WARNING_THRESHOLD)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    /**
     * Update budget limit
     */
    fun updateBudgetLimit(newLimit: Double) {
        _budgetLimit.value = newLimit
    }
}
