package com.example.financeapp.data.repository

import com.example.financeapp.data.local.dao.CategoryDao
import com.example.financeapp.data.local.entity.CategoryEntity
import com.example.financeapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val api: ApiService
) {
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>> {
        return categoryDao.getCategoriesByType(type)
    }

    suspend fun insert(userId: Int, category: CategoryEntity) {
        categoryDao.insertCategory(category)

        // 🛠️ ĐÃ KHỚP MYSQL: Ép kiểu dữ liệu chuỗi thành ENUM('CHI PHÍ','THU NHẬP') viết hoa có dấu
        val mysqlType = if (category.type.lowercase() == "income" || category.type == "THU NHẬP") {
            "THU NHẬP"
        } else {
            "CHI PHÍ"
        }

        try {
            api.addCategory(
                userId = userId,
                categoryName = category.category_name, // Khớp với trường category_name
                type = mysqlType,
                color = category.color ?: "#FFFFFF"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun update(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun delete(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
        try {
            api.deleteCategory(category.category_id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}