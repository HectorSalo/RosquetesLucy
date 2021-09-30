package com.skysam.hchirinos.rosqueteslucy.common.dataClass

/**
 * Created by Hector Chirinos (Home) on 29/9/2021.
 */
data class Refund(
    var id: String,
    var idCostumer: String,
    var nameCostumer: String,
    var location: String,
    var price: Double,
    var isDolar: Boolean,
    var quantity: Int,
    var date: Long,
    var rate: Double
)
