package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import com.skysam.hchirinos.rosqueteslucy.common.dataClass.PrimaryProducts

/**
 * Created by Hector Chirinos (Home) on 1/10/2021.
 */
interface OnClickList {
    fun deleteItem(primaryProducts: PrimaryProducts)
    fun editItem(primaryProducts: PrimaryProducts)
}