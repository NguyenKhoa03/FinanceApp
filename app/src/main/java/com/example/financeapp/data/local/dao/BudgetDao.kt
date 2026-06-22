package com.example.financeapp.data.local.dao

import androidx.room.*
import com.example.financeapp.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    // 🛠️ ĐÃ SỬA: Đổi monthYear thành month_year trong câu lệnh SQL cho đúng Entity mới
    @Query("SELECT * FROM budgets WHERE month_year = :monthYear")
    fun getBudgetsByMonth(monthYear: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    // 🛠️ ĐÃ SỬA: Đổi sang category_id và month_year trong câu lệnh SQL
    @Query("SELECT * FROM budgets WHERE category_id = :categoryId AND month_year = :monthYear LIMIT 1")
    suspend fun getBudgetByCategoryAndMonth(categoryId: Int, monthYear: String): BudgetEntity?
}