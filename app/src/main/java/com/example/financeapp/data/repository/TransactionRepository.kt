package com.example.financeapp.data.repository

import com.example.financeapp.data.local.dao.AccountDao
import com.example.financeapp.data.local.dao.TransactionDao
import com.example.financeapp.data.local.entity.TransactionEntity
import com.example.financeapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class TransactionRepository(
    private val dao: TransactionDao,
    private val accountDao: AccountDao,
    private val api: ApiService
) {
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

    suspend fun insertTransaction(userId: Int, transaction: TransactionEntity) {
        dao.insertTransaction(transaction)

        // Phân loại dòng tiền linh hoạt (hỗ trợ cả chữ thường nếu UI chưa đồng bộ kịp)
        val isIncome = transaction.type.lowercase() == "income" || transaction.type == "THU NHẬP"
        val changeAmount = if (isIncome) transaction.amount else -transaction.amount
        accountDao.updateBalance(transaction.account_id, changeAmount)

        // 🛠️ ĐÃ KHỚP MYSQL: Định dạng lại ngày sang chuỗi yyyy-MM-dd tương thích cột DATE
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(Date(transaction.transaction_date))

        // 🛠️ ĐÃ KHỚP MYSQL: Đồng bộ hóa kiểu ENUM('CHI PHÍ', 'THU NHẬP') viết hoa, có dấu
        val mysqlType = if (isIncome) "THU NHẬP" else "CHI PHÍ"

        try {
            api.addTransaction(
                userId = userId,
                accountId = transaction.account_id,
                categoryId = transaction.category_id,
                note = transaction.note,
                amount = transaction.amount, // Truyền đúng kiểu Long tương thích với cột BIGINT
                type = mysqlType,
                transactionDate = dateString
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Int) {
        dao.deleteTransaction(id)
        try {
            api.deleteTransaction(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}