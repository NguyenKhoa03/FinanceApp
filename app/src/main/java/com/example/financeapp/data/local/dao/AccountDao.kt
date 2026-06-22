package com.example.financeapp.data.local.dao

import androidx.room.*
import com.example.financeapp.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    // 🛠️ ĐÃ THÊM: Chỉ lấy danh sách tài khoản thuộc về User đang đăng nhập để tránh lệch pha ID
    @Query("SELECT * FROM accounts WHERE user_id = :userId")
    fun getAccountsByUserId(userId: Int): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE account_id = :id")
    suspend fun getAccountById(id: Int): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    // 🛠️ ĐÃ THÊM: Tính tổng tiền theo đúng User đang đăng nhập
    @Query("SELECT SUM(balance) FROM accounts WHERE user_id = :userId")
    fun getTotalBalanceByUserId(userId: Int): Flow<Long?>

    @Query("SELECT SUM(balance) FROM accounts")
    fun getTotalBalance(): Flow<Long?>

    @Query("UPDATE accounts SET balance = balance + :amount WHERE account_id = :accountId")
    suspend fun updateBalance(accountId: Int, amount: Long)
}