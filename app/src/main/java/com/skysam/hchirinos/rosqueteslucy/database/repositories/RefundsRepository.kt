package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 29/9/2021.
 */
object RefundsRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.REFUNDS)
    }

    fun addRefund(refund: Refund) {
        val data = hashMapOf(
            Constants.ID_COSTUMER to refund.idCostumer,
            Constants.NAME to refund.nameCostumer,
            Constants.COSTUMER_LOCATION to refund.location,
            Constants.PRICE to refund.price,
            Constants.IS_DOLAR to refund.isDolar,
            Constants.QUANTITY to refund.quantity,
            Constants.DATE_DELIVERY to Date(refund.date),
            Constants.RATE_DELIVERY to refund.rate
        )
        getInstance().add(data)
    }

    fun getRefunds(): Flow<MutableList<Refund>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.DATE_DELIVERY, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val refunds = mutableListOf<Refund>()
                    for (refund in value!!) {
                        val refundNew = Refund(
                            refund.id,
                            refund.getString(Constants.ID_COSTUMER)!!,
                            refund.getString(Constants.NAME)!!,
                            refund.getString(Constants.COSTUMER_LOCATION)!!,
                            refund.getDouble(Constants.PRICE)!!,
                            refund.getBoolean(Constants.IS_DOLAR)!!,
                            refund.getDouble(Constants.QUANTITY)!!.toInt(),
                            refund.getDate(Constants.DATE_DELIVERY)!!.time,
                            refund.getDouble(Constants.RATE_DELIVERY)!!
                        )
                        refunds.add(refundNew)
                    }
                    trySend(refunds)
                }
            awaitClose { request.remove() }
        }
    }

    fun deleteRefund(refund: Refund) {
        getInstance().document(refund.id)
            .delete()
    }

    fun editRefund(refund: Refund) {
        val data: Map<String, Any> = hashMapOf(
            Constants.ID_COSTUMER to refund.idCostumer,
            Constants.NAME to refund.nameCostumer,
            Constants.COSTUMER_LOCATION to refund.location,
            Constants.PRICE to refund.price,
            Constants.IS_DOLAR to refund.isDolar,
            Constants.QUANTITY to refund.quantity,
            Constants.DATE_DELIVERY to Date(refund.date),
            Constants.RATE_DELIVERY to refund.rate
        )
        getInstance().document(refund.id)
            .update(data)
    }
}