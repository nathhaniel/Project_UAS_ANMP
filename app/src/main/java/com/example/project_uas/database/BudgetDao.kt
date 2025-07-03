package com.example.project_uas.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.project_uas.model.Budget

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    // Ambil semua budget milik user tertentu
    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY id DESC")
    fun getAllBudgetsByUser(userId: Int): LiveData<List<Budget>>

    @Query("SELECT * FROM budgets WHERE id = :budgetId LIMIT 1")
    suspend fun getBudgetById(budgetId: Int): Budget?

    @Query("SELECT SUM(amount) FROM expenses WHERE budgetId = :budgetId")
    suspend fun getTotalExpenseByBudgetId(budgetId: Int): Int?
    // Ambil total seluruh budget user
    @Query("SELECT SUM(amount) FROM budgets WHERE userId = :userId")
    suspend fun getTotalBudgetByUser(userId: Int): Int?


}
