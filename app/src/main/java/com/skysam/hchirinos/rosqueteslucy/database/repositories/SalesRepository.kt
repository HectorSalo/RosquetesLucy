package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 3/8/2021.
 */
object SalesRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.SALES)
    }

    fun addSale(sale: Sale) {
        val data = hashMapOf(
            Constants.ID_COSTUMER to sale.idCostumer,
            Constants.NAME to sale.nameCostumer,
            Constants.COSTUMER_LOCATION to sale.location,
            Constants.PRICE to sale.price,
            Constants.QUANTITY to sale.quantity,
            Constants.IS_DOLAR to sale.isDolar,
            Constants.NUMBER_INVOICE to sale.invoice,
            Constants.IS_PAID to sale.isPaid,
            Constants.DATE_DELIVERY to Date(sale.date)
        )
        getInstance().add(data)
    }

    fun getSales(): Flow<MutableList<Sale>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.DATE_DELIVERY, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val sales = mutableListOf<Sale>()
                    for (sale in value!!) {
                        val saleNew = Sale(
                            sale.id,
                            sale.getString(Constants.ID_COSTUMER)!!,
                            sale.getString(Constants.NAME)!!,
                            sale.getString(Constants.COSTUMER_LOCATION)!!,
                            sale.getDouble(Constants.PRICE)!!,
                            sale.getDouble(Constants.QUANTITY)!!.toInt(),
                            sale.getBoolean(Constants.IS_DOLAR)!!,
                            sale.getDouble(Constants.NUMBER_INVOICE)!!.toInt(),
                            sale.getBoolean(Constants.IS_PAID)!!,
                            sale.getDate(Constants.DATE_DELIVERY)!!.time
                        )
                        sales.add(saleNew)
                    }
                    offer(sales)
                }
            awaitClose { request.remove() }
        }
    }
}