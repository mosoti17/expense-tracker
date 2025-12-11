# Expense Tracker - Android App

A modern, feature-rich expense tracking application built with Jetpack Compose and Material 3 Design. Track your income and expenses, visualize spending patterns, and manage your monthly budget with an intuitive and beautiful UI.

## Features

### Core Features
- **Transaction Management**
  - Add income and expense transactions with detailed information
  - Edit existing transactions
  - Delete transactions with swipe gesture and confirmation
  - View all transactions grouped by date
  - Search transactions by description or category
  - Filter transactions by type, category, and date range

- **Categories**
  - **Expense Categories**: Food & Dining, Transportation, Shopping, Bills & Utilities, Entertainment, Healthcare, Education, Others
  - **Income Categories**: Salary, Freelance, Business, Investments, Others
  - Each category has a distinct icon and color for easy identification

- **Dashboard/Summary**
  - Current month's total income, expenses, and balance
  - Recent transactions view
  - Quick stats showing highest expense and most used category
  - Animated summary cards

- **Statistics & Analytics**
  - Interactive pie charts showing expense breakdown by category
  - Category-wise spending with percentage breakdown
  - Animated progress bars for each category
  - Toggle between expense and income views
  - Filter by week or month

- **Budget Management**
  - Set monthly budget limit
  - Visual progress indicator showing budget usage
  - Warning notifications when approaching or exceeding budget
  - Helpful budget tips
  - Real-time budget tracking

### UI/UX Features
- **Material 3 Design** with dynamic theming support
- **Smooth animations** for all interactions
- **Swipe to delete** with confirmation dialogs
- **LazyColumn** for efficient list rendering
- **Sticky headers** for date grouping
- **Bottom Navigation** for easy screen switching
- **Modal Bottom Sheets** for adding/editing transactions
- **Date Picker** for transaction date selection
- **Filter Chips** for quick filtering
- **Animated charts** with smooth transitions

## Technical Stack

### Architecture & Components
- **MVVM Architecture** - Clean separation of concerns
- **Jetpack Compose** - Modern declarative UI framework
- **Room Database** - Local data persistence
- **Kotlin Coroutines** - Asynchronous operations
- **Flow & StateFlow** - Reactive data streams
- **Navigation Compose** - Screen navigation
- **Material 3** - Latest Material Design components

### Database Schema

#### Transaction Table
```kotlin
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: String,
    val type: TransactionType, // INCOME or EXPENSE
    val description: String,
    val date: Long, // Timestamp
    val createdAt: Long = System.currentTimeMillis()
)
```

### Project Structure
```
com.example.expensetracker/
├── data/
│   ├── model/
│   │   ├── Transaction.kt          # Transaction entity
│   │   ├── Category.kt             # Category sealed class
│   │   └── TransactionType.kt      # Transaction type enum
│   ├── database/
│   │   ├── ExpenseDatabase.kt      # Room database
│   │   ├── TransactionDao.kt       # Data access object
│   │   └── Converters.kt           # Type converters
│   └── repository/
│       └── TransactionRepository.kt # Repository pattern
├── ui/
│   ├── theme/
│   │   ├── Color.kt                # App colors
│   │   ├── Theme.kt                # App theme
│   │   └── Type.kt                 # Typography
│   ├── components/
│   │   ├── TransactionCard.kt      # Transaction list item
│   │   ├── SummaryCard.kt          # Summary display
│   │   ├── CategoryIcon.kt         # Category icon with background
│   │   └── AddTransactionSheet.kt  # Add/Edit transaction UI
│   ├── screens/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt       # Dashboard screen
│   │   │   └── HomeViewModel.kt    # Home ViewModel
│   │   ├── transactions/
│   │   │   ├── TransactionsScreen.kt
│   │   │   └── TransactionsViewModel.kt
│   │   ├── statistics/
│   │   │   ├── StatisticsScreen.kt
│   │   │   └── StatisticsViewModel.kt
│   │   └── budget/
│   │       ├── BudgetScreen.kt
│   │       └── BudgetViewModel.kt
│   └── navigation/
│       └── AppNavigation.kt        # Navigation setup
├── util/
│   ├── Constants.kt                # App constants
│   ├── DateUtils.kt                # Date utilities
│   └── CurrencyUtils.kt            # Currency formatting
└── MainActivity.kt                  # Entry point
```

## Key Compose Patterns Used

### 1. State Management with StateFlow
```kotlin
val transactions: StateFlow<List<Transaction>> = repository.allTransactions
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
```

### 2. Swipe to Dismiss
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
SwipeToDismissBox(
    state = dismissState,
    backgroundContent = { /* Delete background */ },
    enableDismissFromStartToEnd = false,
    enableDismissFromEndToStart = true
) {
    TransactionCard(transaction)
}
```

### 3. LazyColumn with Grouped Items
```kotlin
LazyColumn {
    groupedTransactions.forEach { (date, transactions) ->
        stickyHeader {
            DateHeader(date)
        }
        items(transactions, key = { it.id }) { transaction ->
            TransactionCard(
                transaction = transaction,
                modifier = Modifier.animateItem()
            )
        }
    }
}
```

### 4. Animated Content
```kotlin
AnimatedContent(
    targetState = balance,
    transitionSpec = {
        (slideInVertically { height -> height } + fadeIn()).togetherWith(
            slideOutVertically { height -> -height } + fadeOut()
        )
    }
) { targetBalance ->
    Text(text = formatCurrency(targetBalance))
}
```

## How to Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11 or higher
- Android SDK with minimum API 24 (Android 7.0)
- Kotlin 2.0.21 or newer

### Steps
1. Clone the repository
   ```bash
   git clone <repository-url>
   cd expense-tracker
   ```

2. Open the project in Android Studio

3. Sync Gradle files
   - Android Studio will automatically prompt you to sync
   - Or go to: File → Sync Project with Gradle Files

4. Build the project
   - Build → Make Project (Ctrl+F9 / Cmd+F9)

5. Run the app
   - Select an emulator or connected device
   - Run → Run 'app' (Shift+F10 / Ctrl+R)

### Build APK
```bash
./gradlew assembleDebug
```
APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

## Sample Data

The app comes pre-loaded with sample transactions for demonstration:
- **Income**: Salary, freelance work, investment dividends
- **Expenses**: Rent, groceries, transportation, entertainment, healthcare, etc.
- Transactions spread across current and previous month
- Approximately 18 sample transactions

Sample data is automatically added on first launch if the database is empty.

## Dependencies

### Core Dependencies
```kotlin
// Compose
androidx.activity:activity-compose:1.9.3
androidx.compose:compose-bom:2024.11.00
androidx.compose.material3:material3
androidx.compose.material:material-icons-extended

// Lifecycle
androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7
androidx.lifecycle:lifecycle-runtime-compose:2.8.7

// Room
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Navigation
androidx.navigation:navigation-compose:2.8.5

// Charts
com.patrykandpatrick.vico:compose:2.0.0-alpha.35
com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.35
```

## Key Features Implementation Details

### Transaction Operations
- **Add**: Fill out the bottom sheet form and tap "Add"
- **Edit**: Tap on any transaction card to edit
- **Delete**: Swipe left on a transaction and confirm deletion
- **Search**: Use the search icon in the Transactions screen
- **Filter**: Use the filter icon to filter by type, category, or date range

### Statistics Screen
- View pie chart of expense distribution
- See category-wise breakdown with percentages
- Animated progress bars for each category
- Toggle between expense and income analytics
- Filter by "This Week" or "This Month"

### Budget Screen
- Set your monthly budget by tapping the edit icon
- View real-time spending progress
- Get warnings when approaching budget limit (80%)
- Alert when exceeding budget
- View helpful budget management tips

## Code Quality Features

### Best Practices Followed
1. **Single Source of Truth** - Repository pattern
2. **Immutable Data Classes** - All state classes are immutable
3. **Proper State Hoisting** - State managed at appropriate levels
4. **Use of remember and rememberSaveable** - Preventing unnecessary recompositions
5. **Lifecycle Awareness** - Using lifecycle-aware components
6. **Kotlin Coroutines** - Proper async/await patterns
7. **Error Handling** - Input validation and safe operations
8. **Animations** - Smooth, meaningful animations throughout

### Security Considerations
- No hardcoded credentials or API keys
- Input validation for amounts and text fields
- Confirmation dialogs for destructive actions (delete)
- Proper Room database implementation with type converters

## Future Enhancements (Potential Features)

- Export transactions to CSV/PDF
- Recurring transactions support
- Multiple currency support
- Biometric authentication
- Cloud backup and sync
- Custom categories
- Widgets for home screen
- Dark mode toggle
- Advanced charts (line charts, bar charts)
- Budget per category
- Transaction attachments (receipts)
- Multi-language support

