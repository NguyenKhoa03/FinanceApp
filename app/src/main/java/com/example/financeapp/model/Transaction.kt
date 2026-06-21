package com.example.financeapp.model

data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val type: String, // "Income" hoặc "Expense"
    val category: String,
    val date: String
)