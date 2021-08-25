package com.skysam.hchirinos.rosqueteslucy.common

import android.app.Application
import android.content.Context

/**
 * Created by Hector Chirinos (Home) on 2/8/2021.
 */
class RosquetesLucy: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    object RosquetesLucy {
        fun getContext(): Context {
            return appContext
        }
    }
}