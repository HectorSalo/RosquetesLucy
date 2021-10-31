package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Customer
import com.skysam.hchirinos.rosqueteslucy.database.repositories.CostumerRepository

class CostumersViewModel : ViewModel() {

    val costumers: LiveData<MutableList<Customer>> = CostumerRepository.getCostumers().asLiveData()

    fun addCostumer(customer: Customer) {
        CostumerRepository.addCostumer(customer)
    }

    fun editCostumer(customer: Customer) {
        CostumerRepository.editCostumer(customer)
    }

    fun deleteCostumer(customer: Customer) {
        CostumerRepository.deleteCostumer(customer)
    }

    fun deleteLocations(id: String, locations: MutableList<String>) {
        CostumerRepository.deleteLocations(id, locations)
    }
}