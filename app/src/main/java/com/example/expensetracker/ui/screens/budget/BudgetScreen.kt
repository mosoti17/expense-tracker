package com.example.expensetracker.ui.screens.budget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.ui.theme.ExpenseRed
import com.example.expensetracker.ui.theme.IncomeGreen
import com.example.expensetracker.util.CurrencyUtils
import com.example.expensetracker.util.DateUtils

/**
 * Budget Screen - Shows monthly budget tracking and spending progress
 *
 * @param viewModel BudgetViewModel instance
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier
) {
    val budgetLimit by viewModel.budgetLimit.collectAsStateWithLifecycle()
    val monthlySpending by viewModel.monthlySpending.collectAsStateWithLifecycle()
    val remainingBudget by viewModel.remainingBudget.collectAsStateWithLifecycle()
    val budgetProgress by viewModel.budgetProgress.collectAsStateWithLifecycle()
    val isOverBudget by viewModel.isOverBudget.collectAsStateWithLifecycle()
    val isApproachingBudget by viewModel.isApproachingBudget.collectAsStateWithLifecycle()

    var showEditBudgetDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Budget",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { showEditBudgetDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Budget",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                modifier = Modifier.height(64.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Month indicator
            Text(
                text = DateUtils.getCurrentMonthYear(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Budget overview card
            BudgetOverviewCard(
                budgetLimit = budgetLimit,
                monthlySpending = monthlySpending,
                remainingBudget = remainingBudget,
                progress = budgetProgress,
                isOverBudget = isOverBudget
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Warning card
            if (isOverBudget) {
                WarningCard(
                    message = "You've exceeded your budget!",
                    type = WarningType.OVER_BUDGET
                )
            } else if (isApproachingBudget) {
                WarningCard(
                    message = "You're approaching your budget limit",
                    type = WarningType.APPROACHING
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Budget tips
            BudgetTipsSection()
        }
    }

    // Edit budget dialog
    if (showEditBudgetDialog) {
        EditBudgetDialog(
            currentBudget = budgetLimit,
            onDismiss = { showEditBudgetDialog = false },
            onSave = { newBudget ->
                viewModel.updateBudgetLimit(newBudget)
                showEditBudgetDialog = false
            }
        )
    }
}

/**
 * Budget overview card with progress indicator
 */
@Composable
private fun BudgetOverviewCard(
    budgetLimit: Double,
    monthlySpending: Double,
    remainingBudget: Double,
    progress: Float,
    isOverBudget: Boolean
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "budget_progress"
    )

    val progressColor by animateColorAsState(
        targetValue = when {
            isOverBudget -> ExpenseRed
            progress > 0.8f -> Color(0xFFFF9800) // Orange
            else -> IncomeGreen
        },
        label = "progress_color"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Budget limit
            Text(
                text = "Monthly Budget",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = CurrencyUtils.formatCurrency(budgetLimit),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                color = progressColor,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Spending and Remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Spent",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = CurrencyUtils.formatCurrency(monthlySpending),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseRed
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Remaining",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = if (remainingBudget >= 0)
                            CurrencyUtils.formatCurrency(remainingBudget)
                        else
                            "-${CurrencyUtils.formatCurrency(-remainingBudget)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (remainingBudget >= 0) IncomeGreen else ExpenseRed
                    )
                }
            }
        }
    }
}

/**
 * Warning types
 */
enum class WarningType {
    OVER_BUDGET,
    APPROACHING
}

/**
 * Warning card
 */
@Composable
private fun WarningCard(
    message: String,
    type: WarningType
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (type) {
                WarningType.OVER_BUDGET -> ExpenseRed.copy(alpha = 0.1f)
                WarningType.APPROACHING -> Color(0xFFFF9800).copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = when (type) {
                    WarningType.OVER_BUDGET -> ExpenseRed
                    WarningType.APPROACHING -> Color(0xFFFF9800)
                },
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = when (type) {
                    WarningType.OVER_BUDGET -> ExpenseRed
                    WarningType.APPROACHING -> Color(0xFFFF9800)
                }
            )
        }
    }
}

/**
 * Budget tips section
 */
@Composable
private fun BudgetTipsSection() {
    Column {
        Text(
            text = "Budget Tips",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val tips = listOf(
            "Track every expense, no matter how small",
            "Review your spending weekly",
            "Set aside savings before budgeting for expenses",
            "Use categories to identify spending patterns",
            "Adjust your budget based on actual spending"
        )

        tips.forEach { tip ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "• $tip",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

/**
 * Edit budget dialog
 */
@Composable
private fun EditBudgetDialog(
    currentBudget: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var budgetText by remember { mutableStateOf(currentBudget.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Monthly Budget") },
        text = {
            OutlinedTextField(
                value = budgetText,
                onValueChange = { budgetText = it },
                label = { Text("Budget Amount (KSh)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Text("KSh", style = MaterialTheme.typography.titleMedium) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newBudget = budgetText.toDoubleOrNull()
                    if (newBudget != null && newBudget > 0) {
                        onSave(newBudget)
                    }
                },
                enabled = budgetText.toDoubleOrNull() != null && budgetText.toDoubleOrNull()!! > 0
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
