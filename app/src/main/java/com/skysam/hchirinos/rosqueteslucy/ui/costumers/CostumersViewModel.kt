package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Location
import com.skysam.hchirinos.rosqueteslucy.database.repositories.CostumerRepository

class CostumersViewModel : ViewModel() {

    val costumers: LiveData<MutableList<Costumer>> = CostumerRepository.getCostumers().asLiveData()

    fun addCostumer(costumer: Costumer) {
        CostumerRepository.addCostumer(costumer)
    }

    fun addLocation(id: String, location: String) {
        CostumerRepository.addLocation(id, location)
    }

    fun editCostumer(costumer: Costumer) {
        CostumerRepository.editCostumer(costumer)
    }

    fun deleteCostumer(costumer: Costumer) {
        CostumerRepository.deleteCostumer(costumer)
    }

    fun deleteLocations(locations: MutableList<Location>) {
        CostumerRepository.deleteLocations(locations)
    }
}