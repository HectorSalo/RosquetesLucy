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

    fun subscribeToTopicUpdateApp() {
        getInstance().subscribeToTopic(Constants.TOPIC_NOTIFICATION_UPDATE_APP)
            .addOnSuccessListener {
                Log.e("MSG OK", "subscribe")
            }
    }

    fun unsubscribeToTopicUpdateApp() {
        getInstance().unsubscribeFromTopic(Constants.TOPIC_NOTIFICATION_UPDATE_APP)
    }

    fun subscribeToTopicSalePaid() {
        getInstance().subscribeToTopic(Constants.TOPIC_NOTIFICATION_SALE_PAID)
            .addOnSuccessListener {
                Log.e("MSG OK", "subscribe")
            }
    }

    fun unsubscribeToTopicSalePaid() {
        getInstance().unsubscribeFromTopic(Constants.TOPIC_NOTIFICATION_SALE_PAID)
    }

    fun subscribeToTopicNoteSalePaid() {
        getInstance().subscribeToTopic(Constants.TOPIC_NOTIFICATION_NOTE_SALE_PAID)
            .addOnSuccessListener {
                Log.e("MSG OK", "subscribe")
            }
    }

    fun unsubscribeToTopicNoteSalePaid() {
        getInstance().unsubscribeFromTopic(Constants.TOPIC_NOTIFICATION_NOTE_SALE_PAID)
    }
}