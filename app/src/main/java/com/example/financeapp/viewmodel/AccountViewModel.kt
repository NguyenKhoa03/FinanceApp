package com.example.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.entity.AccountEntity
import com.example.financeapp.data.repository.AccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountViewModel(private val repository: AccountRepository) : ViewModel() {

    val allAccounts: StateFlow<List<AccountEntity>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBalance: StateFlow<Double?> = repository.totalBalance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addAccount(name: String, balance: Double) {
        viewModelScope.launch {
            val newAccount = AccountEntity(id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(), name = name, balance = balance)
            repository.insert(newAccount)
        }
    }

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.update(account)
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.delete(account)
        }
    }
}

class AccountViewModelFactory(private val repository: AccountRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}