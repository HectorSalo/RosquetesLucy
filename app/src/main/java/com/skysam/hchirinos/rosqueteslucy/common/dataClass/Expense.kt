package com.skysam.hchirinos.rosqueteslucy.common.dataClass

/**
 * Created by Hector Chirinos (Home) on 30/9/2021.
 */
data class Expense(
    var id: String,
    var nameSupplier: String,
    var idSupplier: String,
    var listProducts: MutableList<PrimaryProducts>,
    var total: Double,
    var dateCreated: Long,
    var rate: Double
)
