package com.example.financeapp.data.repository

import com.example.financeapp.data.local.dao.CategoryDao
import com.example.financeapp.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>> {
        return categoryDao.getCategoriesByType(type)
    }

    suspend fun insert(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    suspend fun update(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun delete(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }
}