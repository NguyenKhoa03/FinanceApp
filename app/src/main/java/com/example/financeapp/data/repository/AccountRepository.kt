package com.example.financeapp.data.repository

import com.example.financeapp.data.local.dao.AccountDao
import com.example.financeapp.data.local.entity.AccountEntity
import com.example.financeapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class AccountRepository(
    private val accountDao: AccountDao,
    private val api: ApiService
) {
    val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()

    fun getAccountsForUser(userId: Int): Flow<List<AccountEntity>> {
        return accountDao.getAccountsByUserId(userId)
    }

    suspend fun insert(userId: Int, account: AccountEntity) {
        try {
            val response = api.addAccount(
                userId = userId,
                accountName = account.account_name,
                accountType = account.account_type,
                balance = account.balance
            )

            val success = response["success"] as? Boolean ?: (response["success"]?.toString() == "true")

            val finalAccount = if (success) {
                val mysqlId = when (val idValue = response["account_id"]) {
                    is Double -> idValue.toInt()
                    is Int -> idValue
                    is String -> idValue.toIntOrNull() ?: account.account_id
                    else -> account.account_id
                }
                account.copy(account_id = mysqlId)
            } else {
                account
            }

            accountDao.insertAccount(finalAccount)

        } catch (e: Exception) {
            e.printStackTrace()
            accountDao.insertAccount(account)
        }
    }

    suspend fun update(account: AccountEntity) {
        accountDao.updateAccount(account)
        try {
            api.updateAccount(
                accountId = account.account_id,
                accountName = account.account_name,
                accountType = account.account_type,
                balance = account.balance
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 🛠️ ĐÃ CẬP NHẬT: Đảm bảo thực thi tác vụ mạng đồng bộ một cách an toàn nhất
    suspend fun delete(account: AccountEntity) {
        // 1. Xóa sạch ở SQLite cục bộ để cập nhật giao diện lập tức
        accountDao.deleteAccount(account)

        // 2. Đồng thời gửi yêu cầu xóa lên server MySQL từ xa
        try {
            val response = api.deleteAccount(account.account_id)
            android.util.Log.d("API_DELETE", "Kết quả xóa từ MySQL: $response")
        } catch (e: Exception) {
            android.util.Log.e("API_DELETE", "Lỗi mạng, chưa xóa được trên MySQL: ${e.message}")
            e.printStackTrace()
        }
    }
}