package com.example.project_uas.viewmodel

import androidx.lifecycle.*
import com.example.project_uas.model.Expense
import com.example.project_uas.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val _userId = MutableLiveData<Int>()

    val allExpenses: LiveData<List<Expense>> = _userId.switchMap { id ->
        repository.getAllExpenses(id)
    }

    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }

    fun getExpensesByBudgetId(budgetId: Int): LiveData<List<Expense>> {
        return repository.getExpensesByBudgetId(budgetId)
    }

    fun getExpenseById(expenseId: Int): LiveData<Expense?> {
        val result = MutableLiveData<Expense?>()
        viewModelScope.launch {
            result.postValue(repository.getExpenseById(expenseId))
        }
        return result
    }

    fun getTotalExpenseByBudgetId(budgetId: Int): LiveData<Int> {
        val result = MutableLiveData<Int>()
        viewModelScope.launch {
            result.postValue(repository.getTotalExpenseByBudgetId(budgetId))
        }
        return result
    }
    fun getAllExpenses(userId: Int): LiveData<List<Expense>> {
        return repository.getAllExpenses(userId)
    }
}
