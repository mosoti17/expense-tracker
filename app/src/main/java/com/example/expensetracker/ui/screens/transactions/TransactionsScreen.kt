package com.example.expensetracker.ui.screens.transactions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.AddTransactionSheet
import com.example.expensetracker.ui.components.TransactionCard
import com.example.expensetracker.util.DateUtils

/**
 * Transactions Screen - Shows all transactions with filtering and search
 *
 * @param viewModel TransactionsViewModel instance
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddTransactionSheet by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            if (showSearchBar) {
                SearchTopBar(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onCloseSearch = {
                        showSearchBar = false
                        viewModel.updateSearchQuery("")
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            "Transactions",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    },
                    actions = {
                        IconButton(onClick = { showSearchBar = true }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    windowInsets = WindowInsets.statusBars
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    transactionToEdit = null
                    showAddTransactionSheet = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Active filters display
            if (uiState.selectedType != null || uiState.selectedCategory != null || uiState.dateFilterType != DateFilterType.ALL) {
                ActiveFiltersRow(
                    uiState = uiState,
                    onClearFilters = { viewModel.clearFilters() }
                )
            }

            // Grouped transactions list
            if (transactions.isEmpty()) {
                EmptyTransactionsView()
            } else {
                GroupedTransactionsList(
                    transactions = transactions,
                    onEdit = {
                        transactionToEdit = it
                        showAddTransactionSheet = true
                    },
                    onDelete = { viewModel.deleteTransaction(it) }
                )
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

    // Filter Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            uiState = uiState,
            onDismiss = { showFilterSheet = false },
            onTypeFilterChange = { viewModel.setTypeFilter(it) },
            onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
            onDateFilterChange = { filterType, start, end ->
                viewModel.setDateFilter(filterType, start, end)
            }
        )
    }
}

/**
 * Search top bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        "Search transactions...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f)
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onCloseSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Close search",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
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

/**
 * Active filters row showing current filters
 */
@Composable
private fun ActiveFiltersRow(
    uiState: TransactionsUiState,
    onClearFilters: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Filters:",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(end = 4.dp)
        )

        if (uiState.selectedType != null) {
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text(uiState.selectedType.name) }
            )
        }

        if (uiState.selectedCategory != null) {
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text(uiState.selectedCategory) }
            )
        }

        if (uiState.dateFilterType != DateFilterType.ALL) {
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text(uiState.dateFilterType.name.replace("_", " ")) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onClearFilters) {
            Text("Clear All")
        }
    }
}

/**
 * Grouped transactions list by date
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GroupedTransactionsList(
    transactions: List<Transaction>,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    // Group transactions by date
    val groupedTransactions = transactions.groupBy {
        DateUtils.formatDateRelative(it.date)
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        groupedTransactions.forEach { (date, transactionsForDate) ->
            // Date header (sticky)
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Transactions for this date
            items(
                items = transactionsForDate,
                key = { it.id }
            ) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

/**
 * Filter bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    uiState: TransactionsUiState,
    onDismiss: () -> Unit,
    onTypeFilterChange: (TransactionType?) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onDateFilterChange: (DateFilterType, Long?, Long?) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Filter Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Type Filter
            Text(
                text = "Transaction Type",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedType == null,
                    onClick = { onTypeFilterChange(null) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = uiState.selectedType == TransactionType.INCOME,
                    onClick = { onTypeFilterChange(TransactionType.INCOME) },
                    label = { Text("Income") }
                )
                FilterChip(
                    selected = uiState.selectedType == TransactionType.EXPENSE,
                    onClick = { onTypeFilterChange(TransactionType.EXPENSE) },
                    label = { Text("Expense") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Filter
            Text(
                text = "Date Range",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.dateFilterType == DateFilterType.ALL,
                    onClick = { onDateFilterChange(DateFilterType.ALL, null, null) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = uiState.dateFilterType == DateFilterType.THIS_WEEK,
                    onClick = { onDateFilterChange(DateFilterType.THIS_WEEK, null, null) },
                    label = { Text("This Week") }
                )
                FilterChip(
                    selected = uiState.dateFilterType == DateFilterType.THIS_MONTH,
                    onClick = { onDateFilterChange(DateFilterType.THIS_MONTH, null, null) },
                    label = { Text("This Month") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Filters")
            }
        }
    }
}

/**
 * Empty state view
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
            text = "No transactions found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try adjusting your filters or add a new transaction",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}
