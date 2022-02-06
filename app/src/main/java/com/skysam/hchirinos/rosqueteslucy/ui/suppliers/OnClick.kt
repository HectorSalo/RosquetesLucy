package com.skysam.hchirinos.rosqueteslucy.ui.suppliers

import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Supplier

/**
 * Created by Hector Chirinos (Home) on 30/9/2021.
 */
interface OnClick {
    fun addExpense(supplier: Supplier)
    fun editSupplier(supplier: Supplier)
    fun deleteSupplier(supplier: Supplier)
}