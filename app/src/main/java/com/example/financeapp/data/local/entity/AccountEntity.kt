package com.example.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val account_id: Int = 0,
    val user_id: Int,
    val account_name: String,
    val account_type: String,
    val balance: Long,
    val icon_url: String? = null
)