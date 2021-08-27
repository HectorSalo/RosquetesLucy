package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.database.repositories.ExpensesRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class ExpensesViewModel : ViewModel() {
    val expenses: LiveData<MutableList<Expense>> = ExpensesRepository.getExpenses().asLiveData()
    val valueWeb: LiveData<String> = SalesRepository.getValueWeb().asLiveData()

    fun addExpense(expense: Expense) {
        ExpensesRepository.addExpense(expense)
    }

    fun deleteExpense(expense: Expense) {
        ExpensesRepository.deleteExpense(expense)
    }

    fun editExpense(expense: Expense) {
        ExpensesRepository.editExpense(expense)
    }
}