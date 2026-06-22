package com.example.financeapp.data.local.dao

import androidx.room.*
import com.example.financeapp.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: UserEntity)

    // 🛠️ ĐÃ SỬA: password -> password_hash (Khớp 100% với cột bảo mật trong MySQL)
    @Query("SELECT * FROM users WHERE username = :username AND password_hash = :password LIMIT 1")
    suspend fun login(username: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?
}