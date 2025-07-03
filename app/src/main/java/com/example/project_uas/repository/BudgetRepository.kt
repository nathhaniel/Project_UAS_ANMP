package com.example.project_uas.repository

import androidx.lifecycle.LiveData
import com.example.project_uas.database.BudgetDao
import com.example.project_uas.model.Budget

class BudgetRepository(private val budgetDao: BudgetDao) {

    fun getAllBudgets(userId: Int): LiveData<List<Budget>> {
        return budgetDao.getAllBudgetsByUser(userId)
    }

    suspend fun insert(budget: Budget) {
        budgetDao.insert(budget)
    }

    suspend fun update(budget: Budget) {
        budgetDao.update(budget)
    }

    suspend fun delete(budget: Budget) {
        budgetDao.delete(budget)
    }

    suspend fun getBudgetById(id: Int): Budget? {
        return budgetDao.getBudgetById(id)
    }

    suspend fun getTotalExpenseByBudgetId(budgetId: Int): Int {
        return budgetDao.getTotalExpenseByBudgetId(budgetId) ?: 0
    }

    suspend fun getTotalBudgetByUser(userId: Int): Int {
        return budgetDao.getTotalBudgetByUser(userId) ?: 0
        }

}
