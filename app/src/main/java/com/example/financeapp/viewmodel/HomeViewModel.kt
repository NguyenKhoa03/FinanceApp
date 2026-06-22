package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    // 🛠️ ĐÃ SỬA: Ép kiểu dữ liệu số tiền mặc định sang Long (thêm ký tự L phía sau)
    private val _balance = MutableStateFlow(15000000L)
    val balance = _balance.asStateFlow()

    private val _income = MutableStateFlow(20000000L)
    val income = _income.asStateFlow()

    private val _expense = MutableStateFlow(5000000L)
    val expense = _expense.asStateFlow()
}