package com.example.project_uas.viewmodel

import androidx.lifecycle.*
import com.example.project_uas.model.Budget
import com.example.project_uas.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val _userId = MutableLiveData<Int>()

    val allBudgets: LiveData<List<Budget>> = _userId.switchMap { id ->
        repository.getAllBudgets(id)
    }

    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    fun insert(budget: Budget) = viewModelScope.launch {
        repository.insert(budget)
    }

    fun update(budget: Budget) = viewModelScope.launch {
        repository.update(budget)
    }

    fun delete(budget: Budget) = viewModelScope.launch {
        repository.delete(budget)
    }

    fun getBudgetById(id: Int): LiveData<Budget?> {
        val result = MutableLiveData<Budget?>()
        viewModelScope.launch {
            result.postValue(repository.getBudgetById(id))
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
}
