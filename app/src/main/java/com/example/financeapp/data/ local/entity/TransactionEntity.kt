package com.example.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val type: String,
    val date: String
)