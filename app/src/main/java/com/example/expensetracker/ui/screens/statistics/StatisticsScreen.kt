package com.example.expensetracker.ui.screens.statistics

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.ui.components.CategoryIcon
import com.example.expensetracker.ui.screens.transactions.DateFilterType
import com.example.expensetracker.ui.theme.chartColors
import com.example.expensetracker.util.CurrencyUtils
import kotlin.math.cos
import kotlin.math.sin

/**
 * Statistics Screen - Shows expense and income breakdowns with charts
 *
 * @param viewModel StatisticsViewModel instance
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    modifier: Modifier = Modifier
) {
    val selectedFilter by viewModel.selectedDateFilter.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val expenseCategoryData by viewModel.expenseCategoryData.collectAsStateWithLifecycle()
    val incomeCategoryData by viewModel.incomeCategoryData.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Statistics",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Date filter selector
            item {
                DateFilterSelector(
                    selectedFilter = selectedFilter,
                    onFilterChange = { viewModel.setDateFilter(it) }
                )
            }

            // Summary card
            item {
                SummarySection(
                    income = totalIncome,
                    expense = totalExpense
                )
            }

            // Tab selector (Expenses / Income)
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Expenses") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Income") }
                    )
                }
            }

            // Content based on selected tab
            if (selectedTab == 0) {
                // Expenses tab
                if (expenseCategoryData.isNotEmpty()) {
                    item {
                        PieChart(
                            data = expenseCategoryData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .padding(vertical = 16.dp)
                        )
                    }
                    items(expenseCategoryData) { categorySpending ->
                        CategorySpendingItem(
                            categorySpending = categorySpending,
                            total = totalExpense
                        )
                    }
                } else {
                    item {
                        EmptyStateView(message = "No expense data for this period")
                    }
                }
            } else {
                // Income tab
                if (incomeCategoryData.isNotEmpty()) {
                    item {
                        PieChart(
                            data = incomeCategoryData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .padding(vertical = 16.dp)
                        )
                    }
                    items(incomeCategoryData) { categorySpending ->
                        CategorySpendingItem(
                            categorySpending = categorySpending,
                            total = totalIncome
                        )
                    }
                } else {
                    item {
                        EmptyStateView(message = "No income data for this period")
                    }
                }
            }
        }
    }
}

/**
 * Date filter selector
 */
@Composable
private fun DateFilterSelector(
    selectedFilter: DateFilterType,
    onFilterChange: (DateFilterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == DateFilterType.THIS_WEEK,
            onClick = { onFilterChange(DateFilterType.THIS_WEEK) },
            label = { Text("This Week") }
        )
        FilterChip(
            selected = selectedFilter == DateFilterType.THIS_MONTH,
            onClick = { onFilterChange(DateFilterType.THIS_MONTH) },
            label = { Text("This Month") }
        )
    }
}

/**
 * Summary section showing total income and expense
 */
@Composable
private fun SummarySection(
    income: Double,
    expense: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Income",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = CurrencyUtils.formatCurrency(income),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Expense",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = CurrencyUtils.formatCurrency(expense),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF44336)
                )
            }
        }
    }
}

/**
 * Simple pie chart using Canvas
 */
@Composable
private fun PieChart(
    data: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "pie_chart_animation"
    )

    LaunchedEffect(data) {
        animationPlayed = true
    }

    if (data.isEmpty()) return

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2 * 0.8f
            val center = Offset(size.width / 2, size.height / 2)
            var startAngle = -90f

            data.forEachIndexed { index, categorySpending ->
                val sweepAngle = (categorySpending.percentage.toFloat() / 100f) * 360f * animationProgress
                val color = chartColors[index % chartColors.size]

                // Draw pie slice
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    ),
                    size = Size(radius * 2, radius * 2)
                )

                startAngle += sweepAngle
            }

            // Draw white circle in center for donut chart effect
            drawCircle(
                color = Color.White,
                radius = radius * 0.6f,
                center = center
            )
        }
    }
}

/**
 * Category spending item with progress bar
 */
@Composable
private fun CategorySpendingItem(
    categorySpending: CategorySpending,
    total: Double
) {
    val category = Category.fromString(categorySpending.category)
    val progress = (categorySpending.amount / total).toFloat()

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) progress else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "progress_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryIcon(
                        category = category,
                        size = 40.dp,
                        iconSize = 20.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = CurrencyUtils.formatCurrency(categorySpending.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = category.color,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = CurrencyUtils.formatPercentage(categorySpending.percentage),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Empty state view
 */
@Composable
private fun EmptyStateView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
