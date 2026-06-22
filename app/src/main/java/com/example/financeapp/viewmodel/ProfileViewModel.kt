package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    val displayName = userRepository.currentUser.map { user ->
        val name = user?.full_name
        if (name.isNullOrBlank()) {
            user?.username ?: "Người dùng"
        } else {
            name
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Đang tải..."
    )
}