package com.example.financeapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeapp.data.local.dao.*
import com.example.financeapp.data.local.entity.*

@Database(
    entities = [
        TransactionEntity::class,
        UserEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        BudgetEntity::class
    ],
    version = 7, // 🛠️ ĐÃ SỬA: Tăng lên version 7 để Room dọn dẹp cấu trúc bảng cũ
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
}