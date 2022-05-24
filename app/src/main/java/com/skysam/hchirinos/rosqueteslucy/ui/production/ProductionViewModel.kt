package com.skysam.hchirinos.rosqueteslucy.ui.production

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Production
import com.skysam.hchirinos.rosqueteslucy.database.repositories.ProductionRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class ProductionViewModel : ViewModel() {
    val productions: LiveData<MutableList<Production>> = ProductionRepository.getAllProductions().asLiveData()
    val valueWeb: LiveData<String> = SalesRepository.getValueWeb().asLiveData()

    fun addProduction(production: Production) {
        ProductionRepository.addProduction(production)
    }
}