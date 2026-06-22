package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.dao.CategoryDao
import com.example.financeapp.data.local.dao.TransactionDao
import com.example.financeapp.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.*
import java.util.*

data class CategorySummary(
    val category: CategoryEntity?,
    val totalAmount: Long,
    val percentage: Float
)

data class MonthlySummary(
    val monthName: String,
    val income: Long,
    val expense: Long,
    val profit: Long
)

class StatisticsViewModel(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : ViewModel() {

    private val calendar = Calendar.getInstance()

    private val _selectedMonth = MutableStateFlow(calendar.get(Calendar.MONTH))
    val selectedMonth = _selectedMonth.asStateFlow()

    private val _selectedYear = MutableStateFlow(calendar.get(Calendar.YEAR))
    val selectedYear = _selectedYear.asStateFlow()

    val categorySummaries: StateFlow<List<CategorySummary>> = combine(
        _selectedMonth, _selectedYear, categoryDao.getAllCategories()
    ) { month, year, categories ->
        val start = getMonthStartLong(month, year)
        val end = getMonthEndLong(month, year)
        val transactions = transactionDao.getTransactionsByRangeSync(start, end)

        val expenseTransactions = transactions.filter { it.type == "expense" }
        val totalExpense = expenseTransactions.sumOf { it.amount }

        expenseTransactions.groupBy { it.category_id }
            .map { (catId, transList) ->
                val amount = transList.sumOf { it.amount }
                CategorySummary(
                    category = categories.find { it.category_id == catId },
                    totalAmount = amount,
                    percentage = if (totalExpense > 0) (amount.toFloat() / totalExpense) else 0f
                )
            }.sortedByDescending { it.totalAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val yearlySummaries: StateFlow<List<MonthlySummary>> = _selectedYear.flatMapLatest { year ->
        val monthFlows = (0..11).map { month ->
            val start = getMonthStartLong(month, year)
            val end = getMonthEndLong(month, year)
            transactionDao.getTransactionsByRange(start, end).map { transactions ->
                val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
                val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
                MonthlySummary(
                    monthName = "${month + 1}",
                    income = income,
                    expense = expense,
                    profit = income - expense
                )
            }
        }
        combine(monthFlows) { it.toList() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateMonth(month: Int) { _selectedMonth.value = month }
    fun updateYear(year: Int) { _selectedYear.value = year }

    private fun getMonthStartLong(month: Int, year: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getMonthEndLong(month: Int, year: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}