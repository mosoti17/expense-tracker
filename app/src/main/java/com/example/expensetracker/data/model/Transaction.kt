package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a financial transaction
 *
 * @param id Unique identifier for the transaction
 * @param amount Transaction amount (positive for income, positive for expense too - type determines)
 * @param category Category name of the transaction
 * @param type Type of transaction (INCOME or EXPENSE)
 * @param description Optional description/note for the transaction
 * @param date Timestamp when the transaction occurred
 * @param createdAt Timestamp when the transaction was created in the database
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val type: TransactionType,
    val description: String,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)
