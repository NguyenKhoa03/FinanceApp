package com.example.financeapp.data.repository

import com.example.financeapp.data.local.dao.BudgetDao
import com.example.financeapp.data.local.dao.TransactionDao
import com.example.financeapp.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) {
    fun getBudgetsByMonth(monthYear: String): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsByMonth(monthYear)
    }

    suspend fun insertOrUpdateBudget(budget: BudgetEntity) {
        val existing = budgetDao.getBudgetByCategoryAndMonth(budget.categoryId, budget.monthYear)
        if (existing != null) {
            budgetDao.updateBudget(budget.copy(id = existing.id))
        } else {
            budgetDao.insertBudget(budget)
        }
    }

    fun getSpentAmount(categoryId: Int, monthYear: String): Flow<Double?> {
        val (startTime, endTime) = getMonthRange(monthYear)
        return transactionDao.getSpentAmountByCategory(categoryId, startTime, endTime)
    }

    private fun getMonthRange(monthYear: String): Pair<Long, Long> {
        val parts = monthYear.split("/")
        val month = parts[0].toInt() - 1
        val year = parts[1].toInt()
        
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endTime = calendar.timeInMillis
        
        return Pair(startTime, endTime)
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        budgetDao.deleteBudget(budget)
    }
}
