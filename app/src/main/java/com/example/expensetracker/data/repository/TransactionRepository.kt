package com.example.expensetracker.data.repository

import com.example.expensetracker.data.database.TransactionDao
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to transaction data sources
 * Follows the single source of truth principle
 */
class TransactionRepository(private val transactionDao: TransactionDao) {

    /**
     * Get all transactions
     */
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    /**
     * Get transaction by ID
     */
    suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)
    }

    /**
     * Get transactions by type
     */
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type)
    }

    /**
     * Get transactions by category
     */
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByCategory(category)
    }

    /**
     * Get transactions within a date range
     */
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    /**
     * Get transactions by type within a date range
     */
    fun getTransactionsByTypeAndDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByTypeAndDateRange(type, startDate, endDate)
    }

    /**
     * Search transactions
     */
    fun searchTransactions(query: String): Flow<List<Transaction>> {
        return transactionDao.searchTransactions(query)
    }

    /**
     * Get total by type within date range
     */
    fun getTotalByTypeAndDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<Double> {
        return transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate)
    }

    /**
     * Get highest expense
     */
    fun getHighestExpense(): Flow<Transaction?> {
        return transactionDao.getHighestExpense()
    }

    /**
     * Get most used category
     */
    fun getMostUsedCategory(type: TransactionType): Flow<String?> {
        return transactionDao.getMostUsedCategory(type)
    }

    /**
     * Insert a transaction
     */
    suspend fun insert(transaction: Transaction): Long {
        return transactionDao.insert(transaction)
    }

    /**
     * Insert multiple transactions
     */
    suspend fun insertAll(transactions: List<Transaction>) {
        transactionDao.insertAll(transactions)
    }

    /**
     * Update a transaction
     */
    suspend fun update(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    /**
     * Delete a transaction
     */
    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    /**
     * Delete all transactions
     */
    suspend fun deleteAll() {
        transactionDao.deleteAll()
    }

    /**
     * Get transaction count
     */
    fun getTransactionCount(): Flow<Int> {
        return transactionDao.getTransactionCount()
    }
}
