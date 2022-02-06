package com.skysam.hchirinos.rosqueteslucy.common.dataClass

/**
 * Created by Hector Chirinos (Home) on 3/8/2021.
 */
data class Sale(
    var id: String,
    var idCostumer: String,
    var nameCostumer: String,
    var location: String,
    var price: Double,
    var rateDelivery: Double,
    var ratePaid: Double,
    var quantity: Int,
    var isDolar: Boolean,
    var invoice: Int,
    var isPaid: Boolean,
    var dateDelivery: Long,
    var datePaid: Long,
    var isAnnuled: Boolean = false
)