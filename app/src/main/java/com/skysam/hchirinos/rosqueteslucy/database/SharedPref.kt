package com.skysam.hchirinos.rosqueteslucy.database

import android.content.Context
import android.content.SharedPreferences
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.RosquetesLucy
import com.skysam.hchirinos.rosqueteslucy.database.repositories.InitSession

/**
 * Created by Hector Chirinos (Home) on 13/9/2021.
 */
object SharedPref {
    private fun getInstance(): SharedPreferences {
        return RosquetesLucy.RosquetesLucy.getContext()
            .getSharedPreferences(InitSession.getCurrentUser()!!.uid, Context.MODE_PRIVATE)
    }

    fun getDaysExpired(): Int {
        return getInstance().getInt(Constants.PREFERENCES_DAYS_EXPIRED, 7)
    }

    fun changeDaysExpired(days: Int) {
        val editor = getInstance().edit()
        editor.putInt(Constants.PREFERENCES_DAYS_EXPIRED, days)
        editor.apply()
    }

    fun isLock(): Boolean {
        return getInstance().getBoolean(Constants.PREFERENCES_LOCK, false)
    }

    fun changeLock(newLock: Boolean) {
        val editor = getInstance().edit()
        editor.putBoolean(Constants.PREFERENCES_LOCK, newLock)
        editor.apply()
    }

    fun getPinLock(): String {
        return getInstance().getString(Constants.PREFERENCES_PIN_LOCK, "0000")!!
    }

    fun changePinLock(newPin: String) {
        val editor = getInstance().edit()
        editor.putString(Constants.PREFERENCES_PIN_LOCK, newPin)
        editor.apply()
    }
}