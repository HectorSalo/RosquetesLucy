package com.skysam.hchirinos.rosqueteslucy.common

import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Hector Chirinos (Home) on 11/11/2021.
 */
object Notification {
    const val NOTIFICATION_API = "https://skysam.000webhostapp.com/r_lucy/r_lucy_RS.php"
    private const val SEND_NOTIFICATION = "sendNotification"

    fun sendNotification (title: String, message: String, topic: String) {
        val params = JSONObject()
        params.put(Constants.METHOD, SEND_NOTIFICATION)
        params.put(Constants.TITLE, title)
        params.put(Constants.MESSAGE, message)
        params.put(Constants.TOPIC, topic)

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, NOTIFICATION_API, params,
            {response ->
                try {
                    when (response.getInt(Constants.SUCCESS)) {
                        Constants.SEND_NOTIFICATION_SUCCESS -> {
                            Log.e("MSG OK", response.getString(Constants.MESSAGE))
                        }
                        Constants.ERROR_METHOD_NOT_EXIST -> Log.e("MSG ERROR", response.getString(Constants.MESSAGE))
                        else -> Log.e("MSG ERROR", response.getString(Constants.MESSAGE))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("MSG ERROR", response.getString(Constants.MESSAGE))
                }
            }, {error ->
                Log.e("Volley error", error.localizedMessage!!)
                Log.e("MSG ERROR", error.message!!)
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }
        RosquetesLucy.RosquetesLucy.addToReqQueue(jsonObjectRequest)
    }
}