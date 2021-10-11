package com.skysam.hchirinos.rosqueteslucy.ui.common.filterList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.database.repositories.CostumerRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.NoteSaleRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

/**
 * Created by Hector Chirinos (Home) on 9/10/2021.
 */
class FilterListViewModel: ViewModel() {
    val costumers: LiveData<MutableList<Costumer>> = CostumerRepository.getCostumers().asLiveData()
    val sales: LiveData<MutableList<Sale>> = SalesRepository.getSales().asLiveData()
    val notesSales: LiveData<MutableList<NoteSale>> = NoteSaleRepository.getNotesSale().asLiveData()

    fun deleteSale(sale: Sale) {
        SalesRepository.deleteSale(sale)
    }

    fun deleteNoteSale(noteSale: NoteSale) {
        NoteSaleRepository.deleteNoteSale(noteSale)
    }
}