package com.example.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val budget_id: Int = 0,
    val user_id: Int,
    val category_id: Int,
    val limit_amount: Long,
    val month_year: String
)