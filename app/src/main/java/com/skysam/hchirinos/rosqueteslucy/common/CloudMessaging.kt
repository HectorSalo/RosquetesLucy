package com.skysam.hchirinos.rosqueteslucy.common

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Created by Hector Chirinos (Home) on 3/10/2021.
 */
object CloudMessaging {
    private fun getInstance(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    fun subscribeToMyTopic() {
        getInstance().subscribeToTopic(Constants.TOPIC_NOTIFICATION_UPDATE_APP)
            .addOnSuccessListener {
                Log.e("MSG OK", "subscribe")
            }
    }

    fun unsubscribeToMyTopic() {
        getInstance().unsubscribeFromTopic(Constants.TOPIC_NOTIFICATION_UPDATE_APP)
    }
}