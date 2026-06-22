package com.example.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transaction_id: Int = 0,
    val user_id: Int,
    val account_id: Int,
    val category_id: Int,
    val type: String,
    val amount: Long,
    val note: String? = null,
    val transaction_date: Long // Kiểu dữ liệu Long (timestamp) đúng như bạn yêu cầu
)