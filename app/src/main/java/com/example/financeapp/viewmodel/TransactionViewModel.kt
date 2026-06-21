package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.financeapp.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TransactionViewModel : ViewModel() {

    // Danh sách gốc chứa Fake Data ban đầu
    private val _transactions = MutableStateFlow(
        listOf(
            Transaction(1, "Lương tháng", 10000000.0, "Salary", "Income", "20/06/2026"),
            Transaction(2, "Ăn uống", -150000.0, "Food", "Expense", "21/06/2026"),
            Transaction(3, "Xăng xe", -50000.0, "Transport", "Expense", "21/06/2026")
        )
    )

    private val _currentFilter = MutableStateFlow("All")
    val currentFilter = _currentFilter.asStateFlow()

    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(_transactions.value)
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions

    init {
        applyFilter()
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
        applyFilter()
    }

    private fun applyFilter() {
        val filter = _currentFilter.value
        _filteredTransactions.value = if (filter == "All") {
            _transactions.value
        } else {
            _transactions.value.filter { it.type == filter }
        }
    }

    // [CREATE] - Thêm giao dịch mới
    fun addTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value + transaction
        applyFilter()
    }

    // [DELETE] - Xóa giao dịch theo ID
    fun deleteTransaction(id: Int) {
        _transactions.value = _transactions.value.filter { it.id != id }
        applyFilter()
    }

    // [UPDATE] - Cập nhật/Sửa giao dịch (Đáp ứng tiêu chí bảo vệ đồ án)
    fun updateTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value.map {
            if (it.id == transaction.id) transaction else it
        }
        applyFilter()
    }
}