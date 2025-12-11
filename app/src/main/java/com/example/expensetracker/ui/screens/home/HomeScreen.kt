package com.example.expensetracker.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.components.AddTransactionSheet
import com.example.expensetracker.ui.components.CategoryIcon
import com.example.expensetracker.ui.components.SummaryCard
import com.example.expensetracker.ui.components.TransactionCard
import com.example.expensetracker.util.CurrencyUtils
import com.example.expensetracker.util.DateUtils

/**
 * Home Screen - Main dashboard showing summary and recent transactions
 *
 * @param viewModel HomeViewModel instance
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    // Collect state from ViewModel
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val monthlyIncome by viewModel.monthlyIncome.collectAsStateWithLifecycle()
    val monthlyExpense by viewModel.monthlyExpense.collectAsStateWithLifecycle()
    val balance by viewModel.balance.collectAsStateWithLifecycle()
    val highestExpense by viewModel.highestExpense.collectAsStateWithLifecycle()
    val mostUsedCategory by viewModel.mostUsedCategory.collectAsStateWithLifecycle()

    // UI State
    var showAddTransactionSheet by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Expense Tracker",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            DateUtils.getCurrentMonthYear(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
//                modifier = Modifier.height(64.dp),
                windowInsets = WindowInsets.statusBars
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    transactionToEdit = null
                    showAddTransactionSheet = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Summary Card
            item {
                SummaryCard(
                    income = monthlyIncome,
                    expense = monthlyExpense,
                    balance = balance
                )
            }

            // Quick Stats
            item {
                QuickStatsSection(
                    highestExpense = highestExpense,
                    mostUsedCategory = mostUsedCategory
                )
            }

            // Recent Transactions Header
            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Recent Transactions List
            if (recentTransactions.isEmpty()) {
                item {
                    EmptyTransactionsView()
                }
            } else {
                items(
                    items = recentTransactions,
                    key = { it.id }
                ) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        onEdit = {
                            transactionToEdit = it
                            showAddTransactionSheet = true
                        },
                        onDelete = { viewModel.deleteTransaction(it) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }

    // Add/Edit Transaction Sheet
    if (showAddTransactionSheet) {
        AddTransactionSheet(
            transaction = transactionToEdit,
            onDismiss = {
                showAddTransactionSheet = false
                transactionToEdit = null
            },
            onSave = { transaction ->
                if (transactionToEdit != null) {
                    viewModel.updateTransaction(transaction)
                } else {
                    viewModel.addTransaction(transaction)
                }
            }
        )
    }
}

/**
 * Quick stats section showing highest expense and most used category
 */
@Composable
private fun QuickStatsSection(
    highestExpense: Transaction?,
    mostUsedCategory: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Quick Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Highest Expense
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Highest Expense",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (highestExpense != null) {
                        Text(
                            text = CurrencyUtils.formatCurrency(highestExpense.amount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Category.fromString(highestExpense.category).name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        Text(
                            text = "No data",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }

                // Most Used Category
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Most Used",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (mostUsedCategory != null) {
                        val category = Category.fromString(mostUsedCategory)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryIcon(
                                category = category,
                                size = 32.dp,
                                iconSize = 16.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Text(
                            text = "No data",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Empty state view when there are no transactions
 */
@Composable
private fun EmptyTransactionsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No transactions yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap + to add your first transaction",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}
