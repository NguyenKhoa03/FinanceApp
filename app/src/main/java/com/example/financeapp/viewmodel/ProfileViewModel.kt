package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val _username = MutableStateFlow("Nguyễn Văn A")
    val username = _username.asStateFlow()

    private val _email = MutableStateFlow("user@gmail.com")
    val email = _email.asStateFlow()
}