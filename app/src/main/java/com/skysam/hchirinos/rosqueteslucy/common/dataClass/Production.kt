package com.skysam.hchirinos.rosqueteslucy.common.dataClass

import java.util.*

/**
 * Created by Hector Chirinos on 23/05/2022.
 */

data class Production(
 val id: String,
 var quantity: Int,
 var date: Date,
 var price: Double,
 var isDolar: Boolean,
 var rate: Double
)
