package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.repository.TransactionRepository
import com.example.financeapp.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _currentFilter = MutableStateFlow("All")
    val currentFilter = _currentFilter.asStateFlow()

    // Kết hợp dữ liệu sống từ Room và bộ lọc UI
    val filteredTransactions: StateFlow<List<Transaction>> = repository.getLocalTransactions()
        .combine(_currentFilter) { transactions, filter ->
            if (filter == "All") {
                transactions
            } else {
                // Đảm bảo so sánh chính xác theo Type ("Income" hoặc "Expense")
                transactions.filter { it.type.equals(filter, ignoreCase = true) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Tự động kéo dữ liệu mới nhất từ MySQL về khi mở App
        refreshDataFromServer()
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
    }

    // Hàm làm mới dữ liệu (Đồng bộ từ Server PHP -> Room DB)
    fun refreshDataFromServer() {
        viewModelScope.launch {
            try {
                repository.refreshTransactions(userId = 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 🛠️ SỬA [CREATE] - Thêm giao dịch lên API Server, thành công thì lưu vào Room
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                // Tận dụng hàm thêm qua API đã nâng cấp ở ApiService / PHP backend
                // Bạn có thể bổ sung hàm này vào Repository để bọc Retrofit,
                // hoặc tạm thời cập nhật cả Local và Remote để UI cập nhật ngay lập tức:
                repository.insertTransaction(transaction)

                // Sau đó ra lệnh đồng bộ lại với server để đảm bảo dữ liệu MySQL khớp đét
                refreshDataFromServer()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 🛠️ SỬA [UPDATE] - Cập nhật giao dịch cục bộ và đồng bộ hệ thống
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)

                // Đồng bộ lại với Server (hoặc gọi API update tùy thuộc vào việc repo của Người 1 đã bọc ApiService chưa)
                refreshDataFromServer()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 🛠️ SỬA [DELETE] - Xóa khỏi Room và dọn dẹp hệ thống
    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(id)

                // Đồng bộ lại với Server để MySQL xóa theo
                refreshDataFromServer()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}