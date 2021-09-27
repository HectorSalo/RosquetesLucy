package com.skysam.hchirinos.rosqueteslucy.common

import java.util.*

/**
 * Created by Hector Chirinos (Home) on 23/9/2021.
 */
object ClassesCommon {
    fun convertFloatToString(value: Float): String {
        return String.format(Locale.GERMANY, "%,.2f", value)
    }

    fun convertDoubleToString(value: Double): String {
        return String.format(Locale.GERMANY, "%,.2f", value)
    }
}