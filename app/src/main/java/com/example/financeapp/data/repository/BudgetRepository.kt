package com.example.financeapp.data.repository

import com.example.financeapp.data.local.dao.BudgetDao
import com.example.financeapp.data.local.dao.TransactionDao
import com.example.financeapp.data.local.entity.BudgetEntity
import com.example.financeapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import java.util.*

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao,
    private val api: ApiService
) {
    fun getBudgetsByMonth(monthYear: String): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsByMonth(monthYear)
    }

    suspend fun insertOrUpdateBudget(userId: Int, budget: BudgetEntity) {
        val existing = budgetDao.getBudgetByCategoryAndMonth(budget.category_id, budget.month_year)
        if (existing != null) {
            budgetDao.updateBudget(budget.copy(budget_id = existing.budget_id))
        } else {
            budgetDao.insertBudget(budget)
        }

        try {
            api.addBudget(
                userId = userId,
                categoryId = budget.category_id,
                limitAmount = budget.limit_amount,
                monthYear = budget.month_year
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSpentAmount(categoryId: Int, monthYear: String): Flow<Long?> {
        val (startTime, endTime) = getMonthRangeLongs(monthYear)
        return transactionDao.getSpentAmountByCategory(categoryId, startTime, endTime)
    }

    private fun getMonthRangeLongs(monthYear: String): Pair<Long, Long> {
        val parts = monthYear.split("/")
        if (parts.size < 2) return Pair(0L, System.currentTimeMillis())

        try {
            val month = parts[0].toInt() - 1
            val year = parts[1].toInt()
            val calendar = Calendar.getInstance()

            calendar.set(year, month, 1, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startLong = calendar.timeInMillis

            calendar.set(year, month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endLong = calendar.timeInMillis

            return Pair(startLong, endLong)
        } catch (e: Exception) {
            return Pair(0L, System.currentTimeMillis())
        }
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        budgetDao.deleteBudget(budget)
    }
}