package com.example.financeapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeapp.data.local.dao.TransactionDao
import com.example.financeapp.data.local.entity.AccountEntity
import com.example.financeapp.data.local.entity.CategoryEntity
import com.example.financeapp.data.local.entity.TransactionEntity
import com.example.financeapp.data.local.entity.UserEntity

@Database(
    entities = [
        TransactionEntity::class,
        UserEntity::class,
        CategoryEntity::class,
        AccountEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}