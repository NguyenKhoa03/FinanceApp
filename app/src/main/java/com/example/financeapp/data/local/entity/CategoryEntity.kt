package com.example.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val category_id: Int = 0,
    val user_id: Int,
    val category_name: String,
    val type: String, // Nhận giá trị chuẩn từ UI: "CHI PHÍ" hoặc "THU NHẬP"
    val icon_name: String? = null,
    val color: String? = null,
    val budget_limit: Long? = null,
    val is_system: Boolean = false
)