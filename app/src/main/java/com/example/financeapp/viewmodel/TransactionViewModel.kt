package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.entity.TransactionEntity
import com.example.financeapp.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            // Đã sửa đổi lấy động ID người dùng thực tế từ thực thể giao dịch do màn hình UI gửi xuống
            repository.insertTransaction(userId = transaction.user_id, transaction = transaction)
        }
    }

    fun deleteTransaction(transactionId: Int) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionId)
        }
    }
}