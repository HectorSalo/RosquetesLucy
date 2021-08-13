package com.skysam.hchirinos.rosqueteslucy.ui.sales

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.database.repositories.CostumerRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class SalesViewModel : ViewModel() {

    val costumers: LiveData<MutableList<Costumer>> = CostumerRepository.getCostumers().asLiveData()

    fun addSale(sale: Sale) {
        SalesRepository.addSale(sale)
    }
}