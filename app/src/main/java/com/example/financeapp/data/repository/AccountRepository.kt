package com.example.financeapp.data.repository

import com.example.financeapp.data.local.dao.AccountDao
import com.example.financeapp.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

class AccountRepository(private val accountDao: AccountDao) {
    val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()
    val totalBalance: Flow<Double?> = accountDao.getTotalBalance()

    suspend fun insert(account: AccountEntity) {
        accountDao.insertAccount(account)
    }

    suspend fun update(account: AccountEntity) {
        accountDao.updateAccount(account)
    }

    suspend fun delete(account: AccountEntity) {
        accountDao.deleteAccount(account)
    }
}