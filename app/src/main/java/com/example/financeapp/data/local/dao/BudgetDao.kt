package com.example.financeapp.data.local.dao

import androidx.room.*
import com.example.financeapp.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun getBudgetsByMonth(monthYear: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND monthYear = :monthYear LIMIT 1")
    suspend fun getBudgetByCategoryAndMonth(categoryId: Int, monthYear: String): BudgetEntity?
}
