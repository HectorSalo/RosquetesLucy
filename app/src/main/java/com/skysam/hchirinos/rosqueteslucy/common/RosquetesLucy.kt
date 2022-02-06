package com.skysam.hchirinos.rosqueteslucy.common

import android.app.Application
import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Created by Hector Chirinos (Home) on 2/8/2021.
 */
class RosquetesLucy: Application() {
    companion object {
        private lateinit var mRequestQueue: RequestQueue
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

        private fun getmRequestQueue(): RequestQueue {
            mRequestQueue = Volley.newRequestQueue(appContext)
            return mRequestQueue
        }

        fun <T> addToReqQueue(request: Request<T>) {
            request.retryPolicy =
                DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            getmRequestQueue().add(request)
        }
    }
}