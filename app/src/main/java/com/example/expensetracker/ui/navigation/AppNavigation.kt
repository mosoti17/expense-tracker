package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.ui.screens.budget.BudgetScreen
import com.example.expensetracker.ui.screens.budget.BudgetViewModel
import com.example.expensetracker.ui.screens.home.HomeScreen
import com.example.expensetracker.ui.screens.home.HomeViewModel
import com.example.expensetracker.ui.screens.statistics.StatisticsScreen
import com.example.expensetracker.ui.screens.statistics.StatisticsViewModel
import com.example.expensetracker.ui.screens.transactions.TransactionsScreen
import com.example.expensetracker.ui.screens.transactions.TransactionsViewModel

/**
 * Sealed class representing navigation routes
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Transactions : Screen("transactions", "Transactions", Icons.Default.List)
    data object Statistics : Screen("statistics", "Statistics", Icons.Default.PieChart)
    data object Budget : Screen("budget", "Budget", Icons.Default.AccountBalanceWallet)
}

/**
 * Main navigation composable with bottom navigation bar
 *
 * @param repository TransactionRepository instance
 */
@Composable
fun AppNavigation(
    repository: TransactionRepository
) {
    val navController = rememberNavController()

    // Bottom navigation items
    val navigationItems = listOf(
        Screen.Home,
        Screen.Transactions,
        Screen.Statistics,
        Screen.Budget
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                items = navigationItems
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                val viewModel = HomeViewModel(repository)
                HomeScreen(viewModel = viewModel)
            }

            composable(Screen.Transactions.route) {
                val viewModel = TransactionsViewModel(repository)
                TransactionsScreen(viewModel = viewModel)
            }

            composable(Screen.Statistics.route) {
                val viewModel = StatisticsViewModel(repository)
                StatisticsScreen(viewModel = viewModel)
            }

            composable(Screen.Budget.route) {
                val viewModel = BudgetViewModel(repository)
                BudgetScreen(viewModel = viewModel)
            }
        }
    }
}

/**
 * Bottom navigation bar component
 */
@Composable
private fun BottomNavigationBar(
    navController: NavHostController,
    items: List<Screen>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}
