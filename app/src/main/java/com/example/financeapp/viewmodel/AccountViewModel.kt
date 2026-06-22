package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.entity.AccountEntity
import com.example.financeapp.data.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountViewModel(private val accountRepository: AccountRepository) : ViewModel() {

    // Đồng bộ danh sách tài khoản ngân hàng/ví (allAccounts) khớp hoàn toàn với HomeScreen
    private val _allAccounts = MutableStateFlow<List<AccountEntity>>(emptyList())
    val allAccounts: StateFlow<List<AccountEntity>> = _allAccounts.asStateFlow()

    init {
        // Tự động lắng nghe và nạp dữ liệu từ SQLite local/server ngay khi mở app
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            // Lấy dòng chảy dữ liệu (Flow) từ Repository để cập nhật giao diện thời gian thực
            accountRepository.allAccounts.collectLatest { accountsList ->
                _allAccounts.value = accountsList
            }
        }
    }

    // Hàm thêm ví mới kết hợp xử lý mạng của Repository (Yêu cầu truyền vào AccountEntity)
    fun insertAccount(account: AccountEntity) {
        viewModelScope.launch {
            try {
                // Repository cần truyền user_id và thực thể account
                accountRepository.insert(userId = account.user_id, account = account)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Hàm xóa ví ngân hàng đồng bộ dữ liệu cục bộ lẫn máy chủ
    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            try {
                accountRepository.delete(account)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Hàm cập nhật ví ngân hàng
    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            try {
                accountRepository.update(account)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}