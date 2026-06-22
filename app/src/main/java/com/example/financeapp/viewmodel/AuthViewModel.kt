package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.entity.UserEntity
import com.example.financeapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState = _registerState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }

        _loginState.value = AuthState.Loading
        viewModelScope.launch {
            val user = userRepository.login(username, password)
            if (user != null) {
                _loginState.value = AuthState.Success(user)
            } else {
                _loginState.value = AuthState.Error("Login failed: Sai tên đăng nhập hoặc mật khẩu")
            }
        }
    }

    fun register(username: String, password: String, fullname: String = "") {
        if (username.isBlank() || password.isBlank()) {
            _registerState.value = AuthState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }

        _registerState.value = AuthState.Loading
        viewModelScope.launch {
            val success = userRepository.register(UserEntity(username = username, password = password, fullname = fullname))
            if (success) {
                _registerState.value = AuthState.Success(null)
            } else {
                _registerState.value = AuthState.Error("Đăng ký thất bại: Tài khoản đã tồn tại")
            }
        }
    }

    fun resetState() {
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity?) : AuthState()
    data class Error(val message: String) : AuthState()
}