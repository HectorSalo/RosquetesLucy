package com.skysam.hchirinos.rosqueteslucy.ui.notesSale

import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale

/**
 * Created by Hector Chirinos on 14/09/2021.
 */
interface OnClick {
    fun viewNoteSale(noteSale: NoteSale)
    fun deleteNoteSale(noteSale: NoteSale)
}