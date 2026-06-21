package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.financeapp.model.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BudgetViewModel : ViewModel() {

    private val _budgets = MutableStateFlow(
        listOf(
            Budget("Ăn uống", 300000.0, 2000000.0),
            Budget("Giải trí", 100000.0, 1000000.0)
        )
    )

    val budgets = _budgets.asStateFlow()
}