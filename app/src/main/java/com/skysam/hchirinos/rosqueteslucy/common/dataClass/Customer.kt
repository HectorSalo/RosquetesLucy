package com.skysam.hchirinos.rosqueteslucy.common.dataClass

import java.io.Serializable

/**
 * Created by Hector Chirinos (Home) on 2/8/2021.
 */
data class Customer(
    var id: String,
    var name: String,
    var identifier: String,
    var address: String,
    var locations: MutableList<String>
) : Serializable
