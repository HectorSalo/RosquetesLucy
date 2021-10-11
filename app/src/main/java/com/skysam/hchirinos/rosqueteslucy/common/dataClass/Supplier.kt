package com.skysam.hchirinos.rosqueteslucy.common.dataClass

/**
 * Created by Hector Chirinos (Home) on 30/9/2021.
 */
data class Supplier(
    var id: String,
    var name: String,
    var locations: MutableList<String>
)
