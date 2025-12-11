package com.example.expensetracker.data.database

import androidx.room.*
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Transaction entity
 * Provides methods to interact with the transactions table
 */
@Dao
interface TransactionDao {

    /**
     * Get all transactions ordered by date descending
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * Get transaction by ID
     */
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    /**
     * Get transactions by type
     */
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    /**
     * Get transactions by category
     */
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>

    /**
     * Get transactions within a date range
     */
    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>

    /**
     * Get transactions by type within a date range
     */
    @Query("SELECT * FROM transactions WHERE type = :type AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getTransactionsByTypeAndDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>>

    /**
     * Search transactions by description
     */
    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :searchQuery || '%' ORDER BY date DESC")
    fun searchTransactions(searchQuery: String): Flow<List<Transaction>>

    /**
     * Get total amount by type within a date range
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = :type AND date >= :startDate AND date <= :endDate")
    fun getTotalByTypeAndDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<Double>

    /**
     * Get the highest expense transaction
     */
    @Query("SELECT * FROM transactions WHERE type = 'EXPENSE' ORDER BY amount DESC LIMIT 1")
    fun getHighestExpense(): Flow<Transaction?>

    /**
     * Get most used category
     */
    @Query("SELECT category FROM transactions WHERE type = :type GROUP BY category ORDER BY COUNT(*) DESC LIMIT 1")
    fun getMostUsedCategory(type: TransactionType): Flow<String?>

    /**
     * Insert a new transaction
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    /**
     * Insert multiple transactions
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)

    /**
     * Update an existing transaction
     */
    @Update
    suspend fun update(transaction: Transaction)

    /**
     * Delete a transaction
     */
    @Delete
    suspend fun delete(transaction: Transaction)

    /**
     * Delete all transactions
     */
    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    /**
     * Get count of transactions
     */
    @Query("SELECT COUNT(*) FROM transactions")
    fun getTransactionCount(): Flow<Int>
}
