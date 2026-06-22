package com.example.financeapp.data.local.dao

import androidx.room.*
import com.example.financeapp.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY transaction_date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    // 🛠️ ĐÃ SỬA: Đổi tham số startTime/endTime sang Long để khớp với kiểu Long của transaction_date
    @Query("SELECT * FROM transactions WHERE transaction_date >= :startTime AND transaction_date <= :endTime ORDER BY transaction_date DESC")
    fun getTransactionsByRange(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

    // 🛠️ ĐÃ SỬA: Đổi tham số thời gian sang Long đồng bộ
    @Query("SELECT * FROM transactions WHERE transaction_date >= :startTime AND transaction_date <= :endTime")
    suspend fun getTransactionsByRangeSync(startTime: Long, endTime: Long): List<TransactionEntity>

    // 🛠️ FIX: Đổi từ 'expense' thành 'CHI PHÍ' để tính tổng số tiền đã chi tiêu chính xác theo ENUM mới
    @Query("SELECT SUM(amount) FROM transactions WHERE category_id = :categoryId AND type = 'CHI PHÍ' AND transaction_date >= :startTime AND transaction_date <= :endTime")
    fun getSpentAmountByCategory(categoryId: Int, startTime: Long, endTime: Long): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE transaction_id = :id")
    suspend fun deleteTransaction(id: Int)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("DELETE FROM transactions")
    suspend fun clearAllUserData()
}