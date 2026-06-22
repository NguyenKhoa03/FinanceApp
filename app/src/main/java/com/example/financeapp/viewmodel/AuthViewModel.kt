package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.entity.UserEntity
import com.example.financeapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Cấu trúc AuthState đồng bộ để LoginScreen lấy được thuộc tính 'role'
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState() // Mang chuỗi quyền hạn "USER" hoặc "ADMIN"
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState = _registerState.asStateFlow()

    private val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{9,}$".toRegex()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }

        _loginState.value = AuthState.Loading
        viewModelScope.launch {
            // Khớp chính xác phương thức gọi hàm loginRemote từ Repository của bạn
            val result = userRepository.loginRemote(username, password)
            result.onSuccess { user ->
                // Trích xuất quyền hạn từ thực thể User trả về để truyền tiếp ra ngoài
                _loginState.value = AuthState.Success(user.role)
            }.onFailure { e ->
                _loginState.value = AuthState.Error("${e.message}")
            }
        }
    }

    fun register(username: String, password: String, fullname: String = "") {
        if (username.isBlank() || password.isBlank()) {
            _registerState.value = AuthState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }

        if (username.length <= 8) {
            _registerState.value = AuthState.Error("Tên đăng nhập phải dài hơn 8 ký tự")
            return
        }

        if (!password.matches(passwordRegex)) {
            _registerState.value = AuthState.Error("Mật khẩu phải dài hơn 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt")
            return
        }

        _registerState.value = AuthState.Loading
        viewModelScope.launch {
            val result = userRepository.registerRemote(username, password, fullname)
            result.onSuccess {
                _registerState.value = AuthState.Success("USER")
            }.onFailure { e ->
                _registerState.value = AuthState.Error("Đăng ký thất bại: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
    }
}
// 🛠️ ĐÃ XÓA KHỐI AuthViewModelFactory TẠI ĐÂY ĐỂ ĐỒNG BỘ VỚI ViewModelFactories.kt, DẬP TẮT LỖI REDECLARATION!