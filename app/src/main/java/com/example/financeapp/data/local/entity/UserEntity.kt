package com.example.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val user_id: Int = 0,

    val username: String,

    val full_name: String,

    val password_hash: String,

    val avatar_url: String? = null,

    val currency: String = "VND",

    val hide_balance: Boolean = false,

    // 🛠️ ĐÃ THÊM: Cột role phân quyền, mặc định đăng ký từ app sẽ là "USER"
    val role: String = "USER"
)