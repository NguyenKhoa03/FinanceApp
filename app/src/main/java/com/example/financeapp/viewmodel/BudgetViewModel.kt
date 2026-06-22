package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.entity.BudgetEntity
import com.example.financeapp.data.local.entity.CategoryEntity
import com.example.financeapp.data.repository.BudgetRepository
import com.example.financeapp.data.repository.CategoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class BudgetWithProgress(
    val budget: BudgetEntity,
    val category: CategoryEntity?,
    val spentAmount: Long
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _currentMonthYear = MutableStateFlow(SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date()))
    val currentMonthYear: StateFlow<String> = _currentMonthYear.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val budgetsWithProgress: StateFlow<List<BudgetWithProgress>> = _currentMonthYear
        .flatMapLatest { monthYear ->
            budgetRepository.getBudgetsByMonth(monthYear).flatMapLatest { budgets ->
                if (budgets.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val flows = budgets.map { budget ->
                        combine(
                            categoryRepository.allCategories.map { categories ->
                                categories.find { it.category_id == budget.category_id }
                            },
                            budgetRepository.getSpentAmount(budget.category_id, monthYear)
                        ) { category, spent ->
                            BudgetWithProgress(budget, category, spent ?: 0L)
                        }
                    }
                    combine(flows) { it.toList() }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addOrUpdateBudget(categoryId: Int, limitAmount: Double) {
        viewModelScope.launch {
            val budget = BudgetEntity(
                user_id = 1,
                category_id = categoryId,
                limit_amount = limitAmount.toLong(),
                month_year = _currentMonthYear.value
            )
            budgetRepository.insertOrUpdateBudget(userId = 1, budget = budget)
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(budget)
        }
    }
}

// 🛠️ ĐA XÓA: Khối class BudgetViewModelFactory ở đây để nhường chỗ cho file tập trung ViewModelFactories.kt xử lý, giải quyết triệt để lỗi Redeclaration!