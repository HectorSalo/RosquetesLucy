package com.skysam.hchirinos.rosqueteslucy.ui.refunds

import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund

/**
 * Created by Hector Chirinos (Home) on 30/9/2021.
 */
interface OnClick {
    fun viewDetails(refund: Refund)
    fun delete(refund: Refund)
}