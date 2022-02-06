package com.skysam.hchirinos.rosqueteslucy.common.dataClass

import java.io.Serializable

/**
 * Created by Hector Chirinos (Home) on 30/9/2021.
 */
data class PrimaryProducts(
    var name: String,
    val unit: String,
    var price: Double,
    var quantity: Double
)
