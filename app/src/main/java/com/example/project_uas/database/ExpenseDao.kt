package com.example.project_uas.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.project_uas.model.Expense

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    // Ambil semua expense milik user tertentu
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpensesByUser(userId: Int): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE budgetId = :budgetId")
    fun getExpensesByBudgetId(budgetId: Int): LiveData<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE budgetId = :budgetId")
    suspend fun getTotalExpenseByBudgetId(budgetId: Int): Int

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Int): Expense?
    // Ambil total semua pengeluaran user
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId")
    suspend fun getTotalExpenseByUser(userId: Int): Int?


}
