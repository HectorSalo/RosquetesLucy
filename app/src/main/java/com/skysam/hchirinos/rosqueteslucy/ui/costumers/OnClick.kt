package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Customer

/**
 * Created by Hector Chirinos (Home) on 2/8/2021.
 */
interface OnClick {
    fun viewCostumer(customer: Customer)
    fun deleteLocation(customer: Customer)
    fun edit(customer: Customer)
    fun delete(customer: Customer)
    fun addRefund(customer: Customer)
    fun viewDocuments(customer: Customer)
}