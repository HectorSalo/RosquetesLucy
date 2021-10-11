package com.skysam.hchirinos.rosqueteslucy.ui.suppliers

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Supplier
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SuppliersRepository

class SuppliersViewModel : ViewModel() {
    val suppliers: LiveData<MutableList<Supplier>> = SuppliersRepository.getSuppliers().asLiveData()

   fun addSupplier(supplier: Supplier) {
       SuppliersRepository.addSupplier(supplier)
   }

    fun deleteSupplier(supplier: Supplier) {
        SuppliersRepository.deleteSupplier(supplier)
    }
}