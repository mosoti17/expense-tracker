package com.example.expensetracker.data.database

import androidx.room.TypeConverter
import com.example.expensetracker.data.model.TransactionType

/**
 * Type converters for Room database
 * Converts custom types to types that Room can persist
 */
class Converters {

    /**
     * Convert TransactionType to String for storage
     */
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    /**
     * Convert String to TransactionType when reading from database
     */
    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}
