package com.example.financeapp

import android.app.Application
import androidx.room.Room
import com.example.financeapp.data.local.database.FinanceDatabase
import com.example.financeapp.data.remote.RetrofitClient
import com.example.financeapp.data.repository.TransactionRepository

class FinanceApplication : Application() {

    // Khởi tạo Database độc bản (Singleton Pattern) dùng chung toàn app
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            FinanceDatabase::class.java,
            "finance_database"
        )
            .fallbackToDestructiveMigration() // Tự động xóa/tạo lại DB nếu đổi cấu hình Entity
            .build()
    }

    val repository by lazy {
        TransactionRepository(database.transactionDao(), RetrofitClient.api)
    }
}