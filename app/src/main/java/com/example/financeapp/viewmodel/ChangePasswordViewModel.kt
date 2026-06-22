package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _state = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val state = _state.asStateFlow()

    private val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{9,}$".toRegex()

    fun changePassword(oldPass: String, newPass: String, confirmPass: String) {
        if (oldPass.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
            _state.value = ChangePasswordState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }

        if (newPass != confirmPass) {
            _state.value = ChangePasswordState.Error("Mật khẩu mới nhập lại không khớp")
            return
        }

        if (!newPass.matches(passwordRegex)) {
            _state.value = ChangePasswordState.Error("Mật khẩu mới phải dài hơn 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt")
            return
        }

        _state.value = ChangePasswordState.Loading
        viewModelScope.launch {
            val result = userRepository.changePassword(oldPass, newPass)
            result.onSuccess {
                _state.value = ChangePasswordState.Success
            }.onFailure { e ->
                _state.value = ChangePasswordState.Error(e.message ?: "Đổi mật khẩu thất bại")
            }
        }
    }

    fun resetState() {
        _state.value = ChangePasswordState.Idle
    }
}

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}