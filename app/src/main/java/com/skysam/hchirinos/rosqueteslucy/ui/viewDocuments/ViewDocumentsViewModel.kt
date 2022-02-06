package com.skysam.hchirinos.rosqueteslucy.ui.viewDocuments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.database.repositories.NoteSaleRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.RefundsRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

/**
 * Created by Hector Chirinos (Home) on 10/10/2021.
 */
class ViewDocumentsViewModel: ViewModel() {
    val allSales: LiveData<MutableList<Sale>> = SalesRepository.getSales().asLiveData()
    val allNotesSales: LiveData<MutableList<NoteSale>> = NoteSaleRepository.getNotesSale().asLiveData()
    val allRefunds: LiveData<MutableList<Refund>> = RefundsRepository.getRefunds().asLiveData()

    private val _costumer = MutableLiveData<Costumer>()
    val costumer: LiveData<Costumer> get() = _costumer

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> get() = _location

    fun addCostumer(costumer: Costumer) {
        _costumer.value = costumer
    }

    fun changeLocation(newLocation: String) {
        _location.value = newLocation
    }

    fun annulSale(sale: Sale) {
        SalesRepository.annulSale(sale)
    }

    fun deleteSale(sale: Sale) {
        SalesRepository.deleteSale(sale)
    }

    fun deleteNoteSale(noteSale: NoteSale) {
        NoteSaleRepository.deleteNoteSale(noteSale)
    }

    fun deleteRefund(refund: Refund) {
        RefundsRepository.deleteRefund(refund)
    }
}