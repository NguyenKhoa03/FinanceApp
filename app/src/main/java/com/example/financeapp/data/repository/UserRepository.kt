package com.example.financeapp.data.repository

import com.example.financeapp.data.local.dao.UserDao
import com.example.financeapp.data.local.entity.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun register(user: UserEntity): Boolean {
        return try {
            val existingUser = userDao.getUserByUsername(user.username)
            if (existingUser != null) return false // User already exists
            userDao.registerUser(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun login(username: String, password: String): UserEntity? {
        return userDao.login(username, password)
    }
}