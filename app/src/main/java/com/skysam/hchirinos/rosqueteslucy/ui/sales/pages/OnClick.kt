package com.skysam.hchirinos.rosqueteslucy.ui.sales.pages

import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale

/**
 * Created by Hector Chirinos (Home) on 15/8/2021.
 */
interface OnClick {
    fun viewSale(sale: Sale)
    fun deleteSale(sale: Sale)
}