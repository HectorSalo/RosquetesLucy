package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.ExpenseOld
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.PrimaryProducts
import com.skysam.hchirinos.rosqueteslucy.database.repositories.ExpensesRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.PrimaryProductsRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class ExpensesViewModel : ViewModel() {
    val expenses: LiveData<MutableList<Expense>> = ExpensesRepository.getExpenses().asLiveData()
    val allProducts: LiveData<MutableList<String>> = PrimaryProductsRepository.getPrimaryProducts().asLiveData()
    val valueWeb: LiveData<String> = SalesRepository.getValueWeb().asLiveData()

    private val _productsInList = MutableLiveData<MutableList<PrimaryProducts>>().apply { value = mutableListOf() }
    val productsInList: LiveData<MutableList<PrimaryProducts>> get() = _productsInList
    private val _priceTotal = MutableLiveData<Double>().apply { value = 0.0 }
    val priceTotal: LiveData<Double> get() = _priceTotal

    fun addExpense(expense: Expense) {
        ExpensesRepository.addExpense(expense)
    }

    fun deleteExpense(expense: Expense) {
        ExpensesRepository.deleteExpense(expense)
    }

    fun editExpense(expenseOld: ExpenseOld) {
        ExpensesRepository.editExpense(expenseOld)
    }

    fun addPrimaryProduct(primaryProducts: PrimaryProducts) {
        if (allProducts.value == null || allProducts.value!!.isEmpty()) {
            PrimaryProductsRepository.addFirstPrimaryProduct(primaryProducts)
        } else {
            PrimaryProductsRepository.addPrimaryProduct(primaryProducts)
        }
        addProductInList(primaryProducts)
    }

    fun addProductInList(primaryProducts: PrimaryProducts) {
        _productsInList.value?.add(primaryProducts)
        _productsInList.value = _productsInList.value
        sumPrice(primaryProducts)
    }

    fun removeProductInList(primaryProducts: PrimaryProducts) {
        _productsInList.value?.remove(primaryProducts)
        _productsInList.value = _productsInList.value
        restPrice(primaryProducts)
    }

    private fun sumPrice(primaryProducts: PrimaryProducts) {
        val subtotal = primaryProducts.quantity * primaryProducts.price
        _priceTotal.value = _priceTotal.value!! + subtotal
    }

    private fun restPrice(primaryProducts: PrimaryProducts) {
        val subtotal = primaryProducts.quantity * primaryProducts.price
        _priceTotal.value = _priceTotal.value!! - subtotal
    }

    fun updateProduct(primaryProducts: PrimaryProducts, position: Int) {
        val productBefore = _productsInList.value?.get(position)
        val priceBefore = productBefore!!.price * productBefore.quantity
        val priceAfter = primaryProducts.price * primaryProducts.quantity
        _productsInList.value?.set(position, primaryProducts)
        _productsInList.value = _productsInList.value
        _priceTotal.value = _priceTotal.value!! + priceAfter - priceBefore
    }
}