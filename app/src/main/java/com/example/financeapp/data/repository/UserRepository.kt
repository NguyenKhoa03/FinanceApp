package com.example.financeapp.data.repository

import com.example.financeapp.data.local.dao.UserDao
import com.example.financeapp.data.local.entity.UserEntity
import com.example.financeapp.data.remote.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(
    private val userDao: UserDao,
    private val api: ApiService
) {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    suspend fun loginRemote(username: String, password: String): Result<UserEntity> {
        return try {
            val response = api.login(username, password)
            val success = response["success"] as? Boolean ?: (response["success"]?.toString() == "true")

            if (success) {
                // PHP có thể trả trực tiếp các trường ra ngoài hoặc bọc trong mảng "user"
                val userData = (response["user"] as? Map<*, *>) ?: response

                val possibleNameKeys = listOf("full_name", "fullname", "name", "display_name")
                var nameFound = ""
                for (key in possibleNameKeys) {
                    val value = userData[key]?.toString()
                    if (!value.isNullOrBlank()) {
                        nameFound = value
                        break
                    }
                }

                if (nameFound.isBlank()) {
                    nameFound = userData["username"]?.toString() ?: username
                }

                val idValue = userData["user_id"] ?: userData["id"]
                val userId = when (idValue) {
                    is Double -> idValue.toInt()
                    is Int -> idValue
                    is String -> idValue.toIntOrNull() ?: 0
                    else -> 0
                }

                // 🛠️ ĐÃ SỬA: Lấy quyền hạn từ PHP gửi về (mặc định là USER nếu trống)
                val roleFound = userData["role"]?.toString() ?: "USER"

                val user = UserEntity(
                    user_id = userId,
                    username = username,
                    password_hash = password,
                    full_name = nameFound,
                    role = roleFound // Đồng bộ sang bảng SQLite Room dưới máy máy
                )

                _currentUser.value = user
                userDao.registerUser(user)
                Result.success(user)
            } else {
                val msg = response["message"]?.toString() ?: "Sai tài khoản hoặc mật khẩu"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerRemote(username: String, password: String, fullname: String): Result<Unit> {
        return try {
            val response = api.register(username, password, fullName = fullname)
            val success = response["success"] as? Boolean ?: (response["success"]?.toString() == "true")
            if (success) {
                Result.success(Unit)
            } else {
                val msg = response["message"]?.toString() ?: "Đăng ký thất bại"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(oldPass: String, newPass: String): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(Exception("Chưa đăng nhập"))
        return try {
            val response = api.changePassword(user.user_id, oldPass, newPass)
            val success = response["success"] as? Boolean ?: (response["success"]?.toString() == "true")
            if (success) {
                val updatedUser = user.copy(password_hash = newPass)
                _currentUser.value = updatedUser
                userDao.registerUser(updatedUser)
                Result.success(Unit)
            } else {
                val msg = response["message"]?.toString() ?: "Mật khẩu cũ không đúng"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        _currentUser.value = null
    }
}