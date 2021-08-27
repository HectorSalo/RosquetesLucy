package com.skysam.hchirinos.rosqueteslucy.common.dataClass

/**
 * Created by Hector Chirinos on 27/08/2021.
 */
data class Expense(
    var id: String,
    var name: String,
    var price: Double,
    var rate: Double,
    var quantity: Double,
    var isDolar: Boolean,
    var date: Long
)
