package com.example.project_uas.repository

import androidx.lifecycle.LiveData
import com.example.project_uas.database.ExpenseDao
import com.example.project_uas.model.Expense

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }

    fun getAllExpenses(userId: Int): LiveData<List<Expense>> {
        return expenseDao.getAllExpensesByUser(userId)
    }

    fun getExpensesByBudgetId(budgetId: Int): LiveData<List<Expense>> {
        return expenseDao.getExpensesByBudgetId(budgetId)
    }

    suspend fun getTotalExpenseByBudgetId(budgetId: Int): Int {
        return expenseDao.getTotalExpenseByBudgetId(budgetId)
    }

    suspend fun getExpenseById(expenseId: Int): Expense? {
        return expenseDao.getExpenseById(expenseId)
    }
    suspend fun getTotalExpenseByUser(userId: Int): Int {
        return expenseDao.getTotalExpenseByUser(userId) ?: 0
    }

}


