package com.skysam.hchirinos.rosqueteslucy.common.dataClass

/**
 * Created by Hector Chirinos (Home) on 3/8/2021.
 */
data class Sales(
    var id: String,
    var idCostumer: String,
    var price: Double,
    var quantity: Int,
    var isDolar: Boolean,
    var invoice: Int,
    var isPaid: Boolean,
    var date: Long
)