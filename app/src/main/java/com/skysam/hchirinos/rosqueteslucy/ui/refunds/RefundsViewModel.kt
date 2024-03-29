package com.skysam.hchirinos.rosqueteslucy.ui.refunds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import com.skysam.hchirinos.rosqueteslucy.database.repositories.CostumerRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.RefundsRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class RefundsViewModel : ViewModel() {
    val valueWeb: LiveData<String> = SalesRepository.getValueWeb().asLiveData()
    val refunds: LiveData<MutableList<Refund>> = RefundsRepository.getRefunds().asLiveData()

    private val _costumer = MutableLiveData<Costumer>()
    val costumer: LiveData<Costumer> get() = _costumer

    private val _addLocation = MutableLiveData<String?>().apply { value = null }
    val addLocation: LiveData<String?> get() = _addLocation

    private val _refundToView = MutableLiveData<Refund>()
    val refundToView: LiveData<Refund> get() = _refundToView

    fun addCostumer(costumer: Costumer) {
        _costumer.value = costumer
    }

    fun addLocation(id: String, location: String) {
        CostumerRepository.addLocation(id, location)
        _costumer.value!!.locations.add(_costumer.value!!.locations.size, location)
        _costumer.value = _costumer.value
        _addLocation.value = location
    }

    fun addRefund(refund: Refund) {
        RefundsRepository.addRefund(refund)
    }

    fun editRefund(refund: Refund) {
        RefundsRepository.editRefund(refund)
    }

    fun deleteRefund(refund: Refund) {
        RefundsRepository.deleteRefund(refund)
    }

    fun viewDetailsRefund(refund: Refund) {
        _refundToView.value = refund
    }
}