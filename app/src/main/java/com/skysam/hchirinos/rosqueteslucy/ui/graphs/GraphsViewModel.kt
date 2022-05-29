package com.skysam.hchirinos.rosqueteslucy.ui.graphs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.*
import com.skysam.hchirinos.rosqueteslucy.database.repositories.*

class GraphsViewModel : ViewModel() {

    val sales: LiveData<MutableList<Sale>> = SalesRepository.getSales().asLiveData()
    val expenses: LiveData<MutableList<Expense>> = ExpensesRepository.getExpenses().asLiveData()
    val notesSale: LiveData<MutableList<NoteSale>> = NoteSaleRepository.getNotesSale().asLiveData()
    val refunds: LiveData<MutableList<Refund>> = RefundsRepository.getRefunds().asLiveData()
    val productions: LiveData<MutableList<Production>> = ProductionRepository.getAllProductions().asLiveData()
}