package com.skysam.hchirinos.rosqueteslucy.ui.graphs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.database.repositories.ExpensesRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.NoteSaleRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.RefundsRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class GraphsViewModel : ViewModel() {

    val sales: LiveData<MutableList<Sale>> = SalesRepository.getSales().asLiveData()
    val expenses: LiveData<MutableList<Expense>> = ExpensesRepository.getExpenses().asLiveData()
    val notesSale: LiveData<MutableList<NoteSale>> = NoteSaleRepository.getNotesSale().asLiveData()
    val refunds: LiveData<MutableList<Refund>> = RefundsRepository.getRefunds().asLiveData()
}