package com.example.expensetracker.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.ui.screens.transactions.DateFilterType
import com.example.expensetracker.util.DateUtils
import kotlinx.coroutines.flow.*

/**
 * Data class representing spending by category
 */
data class CategorySpending(
    val category: String,
    val amount: Double,
    val percentage: Double
)

/**
 * ViewModel for the Statistics Screen
 * Manages statistics data and chart information
 */
class StatisticsViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _selectedDateFilter = MutableStateFlow(DateFilterType.THIS_MONTH)
    val selectedDateFilter: StateFlow<DateFilterType> = _selectedDateFilter.asStateFlow()

    // Get date range based on selected filter
    private val dateRange: StateFlow<Pair<Long, Long>> = _selectedDateFilter.map { filter ->
        when (filter) {
            DateFilterType.THIS_WEEK -> {
                Pair(DateUtils.getStartOfWeek(), DateUtils.getEndOfWeek())
            }
            DateFilterType.THIS_MONTH -> {
                Pair(DateUtils.getStartOfMonth(), DateUtils.getEndOfMonth())
            }
            else -> {
                Pair(DateUtils.getStartOfMonth(), DateUtils.getEndOfMonth())
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Pair(DateUtils.getStartOfMonth(), DateUtils.getEndOfMonth())
    )

    // Total income for selected period
    val totalIncome: StateFlow<Double> = dateRange.flatMapLatest { (start, end) ->
        repository.getTotalByTypeAndDateRange(
            type = TransactionType.INCOME,
            startDate = start,
            endDate = end
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Total expense for selected period
    val totalExpense: StateFlow<Double> = dateRange.flatMapLatest { (start, end) ->
        repository.getTotalByTypeAndDateRange(
            type = TransactionType.EXPENSE,
            startDate = start,
            endDate = end
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Category breakdown for expenses
    val expenseCategoryData: StateFlow<List<CategorySpending>> = combine(
        dateRange,
        totalExpense
    ) { (start, end), total ->
        Pair(Pair(start, end), total)
    }.flatMapLatest { (dateRange, total) ->
        val (start, end) = dateRange
        repository.getTransactionsByTypeAndDateRange(
            type = TransactionType.EXPENSE,
            startDate = start,
            endDate = end
        ).map { transactions ->
            // Group by category and calculate totals
            val categoryTotals = transactions
                .groupBy { it.category }
                .mapValues { (_, trans) -> trans.sumOf { it.amount } }

            // Convert to CategorySpending with percentages
            categoryTotals.map { (category, amount) ->
                CategorySpending(
                    category = category,
                    amount = amount,
                    percentage = if (total > 0) (amount / total) * 100 else 0.0
                )
            }.sortedByDescending { it.amount }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Category breakdown for income
    val incomeCategoryData: StateFlow<List<CategorySpending>> = combine(
        dateRange,
        totalIncome
    ) { (start, end), total ->
        Pair(Pair(start, end), total)
    }.flatMapLatest { (dateRange, total) ->
        val (start, end) = dateRange
        repository.getTransactionsByTypeAndDateRange(
            type = TransactionType.INCOME,
            startDate = start,
            endDate = end
        ).map { transactions ->
            // Group by category and calculate totals
            val categoryTotals = transactions
                .groupBy { it.category }
                .mapValues { (_, trans) -> trans.sumOf { it.amount } }

            // Convert to CategorySpending with percentages
            categoryTotals.map { (category, amount) ->
                CategorySpending(
                    category = category,
                    amount = amount,
                    percentage = if (total > 0) (amount / total) * 100 else 0.0
                )
            }.sortedByDescending { it.amount }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Set date filter
     */
    fun setDateFilter(filter: DateFilterType) {
        _selectedDateFilter.value = filter
    }
}
