package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer

/**
 * Created by Hector Chirinos (Home) on 2/8/2021.
 */
interface OnClick {
    fun viewCostumer(costumer: Costumer)
    fun deleteLocation(costumer: Costumer)
    fun edit(costumer: Costumer)
    fun delete(costumer: Costumer)
    fun addRefund(costumer: Costumer)
}