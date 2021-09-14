package com.skysam.hchirinos.rosqueteslucy.common.dataClass

/**
 * Created by Hector Chirinos on 14/09/2021.
 */
data class NoteSale(
    var id: String,
    var idCostumer: String,
    var nameCostumer: String,
    var location: String,
    var price: Double,
    var rate: Double,
    var quantity: Int,
    var isDolar: Boolean,
    var noteNumber: Int,
    var date: Long
)
