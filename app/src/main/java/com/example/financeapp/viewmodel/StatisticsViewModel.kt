package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.financeapp.model.Statistic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StatisticsViewModel : ViewModel() {

    private val _statistics = MutableStateFlow(
        listOf(
            Statistic("Ăn uống", 300000.0),
            Statistic("Xăng xe", 150000.0),
            Statistic("Giải trí", 500000.0)
        )
    )

    val statistics = _statistics.asStateFlow()
}