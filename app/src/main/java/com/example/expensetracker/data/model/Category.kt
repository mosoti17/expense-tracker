package com.example.expensetracker.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing transaction categories
 * Each category has a name, icon, and color for visual representation
 */
sealed class Category(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val type: TransactionType
) {
    // Expense Categories
    data object Food : Category(
        "Food & Dining",
        Icons.Default.Restaurant,
        Color(0xFFFF9800), // Orange
        TransactionType.EXPENSE
    )

    data object Transport : Category(
        "Transportation",
        Icons.Default.DirectionsCar,
        Color(0xFF2196F3), // Blue
        TransactionType.EXPENSE
    )

    data object Shopping : Category(
        "Shopping",
        Icons.Default.ShoppingBag,
        Color(0xFFE91E63), // Pink
        TransactionType.EXPENSE
    )

    data object Bills : Category(
        "Bills & Utilities",
        Icons.Default.Receipt,
        Color(0xFF9C27B0), // Purple
        TransactionType.EXPENSE
    )

    data object Entertainment : Category(
        "Entertainment",
        Icons.Default.Movie,
        Color(0xFF00BCD4), // Cyan
        TransactionType.EXPENSE
    )

    data object Healthcare : Category(
        "Healthcare",
        Icons.Default.LocalHospital,
        Color(0xFFF44336), // Red
        TransactionType.EXPENSE
    )

    data object Education : Category(
        "Education",
        Icons.Default.School,
        Color(0xFF3F51B5), // Indigo
        TransactionType.EXPENSE
    )

    data object OtherExpense : Category(
        "Other Expenses",
        Icons.Default.MoreHoriz,
        Color(0xFF607D8B), // Blue Grey
        TransactionType.EXPENSE
    )

    // Income Categories
    data object Salary : Category(
        "Salary",
        Icons.Default.AccountBalance,
        Color(0xFF4CAF50), // Green
        TransactionType.INCOME
    )

    data object Freelance : Category(
        "Freelance",
        Icons.Default.Work,
        Color(0xFF8BC34A), // Light Green
        TransactionType.INCOME
    )

    data object Business : Category(
        "Business",
        Icons.Default.Business,
        Color(0xFF009688), // Teal
        TransactionType.INCOME
    )

    data object Investments : Category(
        "Investments",
        Icons.Default.TrendingUp,
        Color(0xFF4CAF50), // Green
        TransactionType.INCOME
    )

    data object OtherIncome : Category(
        "Other Income",
        Icons.Default.AttachMoney,
        Color(0xFF689F38), // Light Green
        TransactionType.INCOME
    )

    companion object {
        /**
         * Get all expense categories
         */
        fun getExpenseCategories(): List<Category> {
            return listOf(
                Food,
                Transport,
                Shopping,
                Bills,
                Entertainment,
                Healthcare,
                Education,
                OtherExpense
            )
        }

        /**
         * Get all income categories
         */
        fun getIncomeCategories(): List<Category> {
            return listOf(
                Salary,
                Freelance,
                Business,
                Investments,
                OtherIncome
            )
        }

        /**
         * Get all categories
         */
        fun getAllCategories(): List<Category> {
            return getExpenseCategories() + getIncomeCategories()
        }

        /**
         * Get category by name
         */
        fun fromString(name: String): Category {
            return when (name) {
                "Food & Dining" -> Food
                "Transportation" -> Transport
                "Shopping" -> Shopping
                "Bills & Utilities" -> Bills
                "Entertainment" -> Entertainment
                "Healthcare" -> Healthcare
                "Education" -> Education
                "Other Expenses" -> OtherExpense
                "Salary" -> Salary
                "Freelance" -> Freelance
                "Business" -> Business
                "Investments" -> Investments
                "Other Income" -> OtherIncome
                else -> OtherExpense
            }
        }
    }
}
