package com.example.financeapp.data.remote

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("transaction_id") val id: Int, // PHP bắt buộc phải trả về "transaction_id"
    val title: String,
    val amount: Double,
    val category: String,
    val type: String,
    val date: String
)