package com.example.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val limitAmount: Double,
    val monthYear: String // Định dạng "MM/yyyy"
)
