package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.database.repositories.CostumerRepository

class CostumersViewModel : ViewModel() {

    val costumers: LiveData<MutableList<Costumer>> = CostumerRepository.getCostumers().asLiveData()

    private val _costumerToUpdate = MutableLiveData<Costumer>()
    val costumerToUpdate: LiveData<Costumer> get() = _costumerToUpdate

    fun addCostumerToEdit(costumer: Costumer) {
        _costumerToUpdate.value = costumer
    }

    fun addCostumer(costumer: Costumer) {
        CostumerRepository.addCostumer(costumer)
    }

    fun editCostumer(costumer: Costumer) {
        CostumerRepository.editCostumer(costumer)
    }

    fun deleteCostumer(costumer: Costumer) {
        CostumerRepository.deleteCostumer(costumer)
    }

    fun deleteLocations(id: String, locations: MutableList<String>) {
        CostumerRepository.deleteLocations(id, locations)
    }
}