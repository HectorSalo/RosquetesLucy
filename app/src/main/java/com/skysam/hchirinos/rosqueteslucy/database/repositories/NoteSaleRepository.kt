package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.Notification
import com.skysam.hchirinos.rosqueteslucy.common.RosquetesLucy
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos on 14/09/2021.
 */
object NoteSaleRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.NOTE_SALES)
    }

    fun addNoteSale(noteSale: NoteSale) {
        val data = hashMapOf(
            Constants.ID_COSTUMER to noteSale.idCostumer,
            Constants.NAME to noteSale.nameCostumer,
            Constants.COSTUMER_LOCATION to noteSale.location,
            Constants.PRICE to noteSale.price,
            Constants.RATE_DELIVERY to noteSale.rateDelivery,
            Constants.RATE_PAID to noteSale.ratePaid,
            Constants.QUANTITY to noteSale.quantity,
            Constants.IS_DOLAR to noteSale.isDolar,
            Constants.NUMBER_NOTE_SALE to noteSale.noteNumber,
            Constants.IS_PAID to noteSale.isPaid,
            Constants.DATE_DELIVERY to Date(noteSale.dateDelivery),
            Constants.DATE_PAID to Date(noteSale.datePaid)
        )
       getInstance().add(data)
    }

    fun getNotesSale(): Flow<MutableList<NoteSale>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.NUMBER_NOTE_SALE, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val notesSale = mutableListOf<NoteSale>()
                    for (noteSale in value!!) {
                        val noteSaleNew = NoteSale(
                            noteSale.id,
                            noteSale.getString(Constants.ID_COSTUMER)!!,
                            noteSale.getString(Constants.NAME)!!,
                            noteSale.getString(Constants.COSTUMER_LOCATION)!!,
                            noteSale.getDouble(Constants.PRICE)!!,
                            noteSale.getDouble(Constants.RATE_DELIVERY)!!,
                            noteSale.getDouble(Constants.RATE_PAID)!!,
                            noteSale.getDouble(Constants.QUANTITY)!!.toInt(),
                            noteSale.getBoolean(Constants.IS_DOLAR)!!,
                            noteSale.getDouble(Constants.NUMBER_NOTE_SALE)!!.toInt(),
                            noteSale.getDate(Constants.DATE_PAID)!!.time,
                            noteSale.getDate(Constants.DATE_DELIVERY)!!.time,
                            noteSale.getBoolean(Constants.IS_PAID)!!
                        )
                        notesSale.add(noteSaleNew)
                    }
                    trySend(notesSale)
                }
            awaitClose { request.remove() }
        }
    }

    fun deleteNoteSale(noteSale: NoteSale) {
        getInstance().document(noteSale.id)
            .delete()
    }

    fun paidNoteSale(noteSale: NoteSale) {
        val data: Map<String, Any> = hashMapOf(
            Constants.IS_PAID to true,
            Constants.DATE_PAID to Date(noteSale.datePaid),
            Constants.RATE_PAID to noteSale.ratePaid
        )
        getInstance().document(noteSale.id)
            .update(data)
            .addOnSuccessListener {
                Notification.sendNotification(
                    RosquetesLucy.RosquetesLucy.getContext().getString(R.string.notification_received_title),
                    RosquetesLucy.RosquetesLucy.getContext().getString(
                        R.string.notification_received_message,
                        RosquetesLucy.RosquetesLucy.getContext().getString(R.string.text_note_sale_single),
                        noteSale.noteNumber.toString()),
                    Constants.TOPIC_NOTIFICATION_NOTE_SALE_PAID)
            }
    }
}