package com.example.financeapp.data.repository

import com.example.financeapp.data.local.dao.TransactionDao
import com.example.financeapp.data.local.entity.TransactionEntity
import com.example.financeapp.data.remote.ApiService
import com.example.financeapp.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val dao: TransactionDao,
    private val api: ApiService
) {

    /**
     * 1. LẤY DỮ LIỆU CỤC BỘ (Room DB)
     * Trả về một luồng dữ liệu sống (Flow). Tự động map từ TransactionEntity thành Transaction (UI)
     */
    fun getLocalTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { entity ->
                Transaction(
                    id = entity.id,
                    title = entity.title,
                    amount = entity.amount,
                    category = entity.category,
                    type = entity.type,
                    date = entity.date
                )
            }
        }
    }

    /**
     * 2. ĐỒNG BỘ DỮ LIỆU TỪ SERVER (PHP API -> Retrofit -> Room DB)
     * Luồng chạy: Gọi API -> Lấy danh sách Response -> Xóa cache cũ ở Local -> Ghi đè danh sách mới vào Room.
     */
    suspend fun refreshTransactions(userId: Int) {
        try {
            // Bước a: Kéo danh sách từ PHP API thông qua Retrofit Client
            val responseList = api.getTransactions(userId)

            // Bước b: Ánh xạ danh sách từ TransactionResponse sang TransactionEntity của Room
            val entityList = responseList.map { response ->
                TransactionEntity(
                    id = response.id,
                    title = response.title,
                    amount = response.amount,
                    category = response.category,
                    type = response.type,
                    date = response.date
                )
            }

            // Bước c: Làm sạch dữ liệu cũ trong Room để tránh trùng lặp hoặc rác
            dao.deleteAll()

            // Bước d: Ghi đè đồng loạt dữ liệu mới nhất từ Server vào DB máy
            entityList.forEach { entity ->
                dao.insertTransaction(entity)
            }
        } catch (e: Exception) {
            // Xử lý ngoại lệ nếu mất mạng hoặc Server PHP XAMPP chưa bật
            e.printStackTrace()
            throw e
        }
    }

    /**
     * 3. THÊM MỚI GIAO DỊCH (Lưu vào Room)
     */
    suspend fun insertTransaction(transaction: Transaction) {
        dao.insertTransaction(
            TransactionEntity(
                id = transaction.id,
                title = transaction.title,
                amount = transaction.amount,
                category = transaction.category,
                type = transaction.type,
                date = transaction.date
            )
        )
    }

    /**
     * 4. CẬP NHẬT/SỬA GIAO DỊCH (Cập nhật vào Room)
     */
    suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(
            TransactionEntity(
                id = transaction.id,
                title = transaction.title,
                amount = transaction.amount,
                category = transaction.category,
                type = transaction.type,
                date = transaction.date
            )
        )
    }

    /**
     * 5. XÓA GIAO DỊCH THEO ID (Xóa khỏi Room)
     */
    suspend fun deleteTransaction(id: Int) {
        dao.deleteTransaction(id)
    }
}