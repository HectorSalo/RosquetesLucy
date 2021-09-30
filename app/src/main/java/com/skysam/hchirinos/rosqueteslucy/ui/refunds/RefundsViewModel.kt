package com.skysam.hchirinos.rosqueteslucy.ui.refunds

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import com.skysam.hchirinos.rosqueteslucy.database.repositories.RefundsRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class RefundsViewModel : ViewModel() {
    val valueWeb: LiveData<String> = SalesRepository.getValueWeb().asLiveData()
    val refunds: LiveData<MutableList<Refund>> = RefundsRepository.getRefunds().asLiveData()

    fun addRefund(refund: Refund) {
        RefundsRepository.addRefund(refund)
    }

    fun editRefund(refund: Refund) {
        RefundsRepository.editRefund(refund)
    }

    fun deleteRefund(refund: Refund) {
        RefundsRepository.deleteRefund(refund)
    }
}