package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _balance = MutableStateFlow(15000000)
    val balance = _balance.asStateFlow()

    private val _income = MutableStateFlow(20000000)
    val income = _income.asStateFlow()

    private val _expense = MutableStateFlow(5000000)
    val expense = _expense.asStateFlow()
}