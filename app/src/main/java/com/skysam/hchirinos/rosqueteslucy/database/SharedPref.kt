package com.skysam.hchirinos.rosqueteslucy.database

import android.content.Context
import android.content.SharedPreferences
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.RosquetesLucy

/**
 * Created by Hector Chirinos (Home) on 13/9/2021.
 */
object SharedPref {
    private fun getInstance(): SharedPreferences {
        return RosquetesLucy.RosquetesLucy.getContext().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
    }

    fun getDaysExpired(): Int {
        return getInstance().getInt(Constants.PREFERENCES_DAYS_EXPIRED, 7)
    }

    fun changeDaysExpired(days: Int) {
        val editor = getInstance().edit()
        editor.putInt(Constants.PREFERENCES_DAYS_EXPIRED, days)
        editor.apply()
    }
}