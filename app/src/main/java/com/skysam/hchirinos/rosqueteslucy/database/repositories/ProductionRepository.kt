package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Production
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos on 23/05/2022.
 */

object ProductionRepository {
 private fun getInstance(): CollectionReference {
  return FirebaseFirestore.getInstance().collection(Constants.PRODUCTIONS)
 }

 fun addProduction(production: Production) {
  val data = hashMapOf(
   Constants.PRICE to production.price,
   Constants.IS_DOLAR to production.isDolar,
   Constants.QUANTITY to production.quantity,
   Constants.DATE to production.date,
   Constants.RATE to production.rate
  )
  getInstance().add(data)
 }

 fun getAllProductions(): Flow<MutableList<Production>> {
  return callbackFlow {
   val request = getInstance()
    .orderBy(Constants.DATE, Query.Direction.DESCENDING)
    .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
     if (error != null) {
      Log.w(ContentValues.TAG, "Listen failed.", error)
      return@addSnapshotListener
     }

     val productions = mutableListOf<Production>()
     for (prod in value!!) {
      val productionNew = Production(
       prod.id,
       prod.getDouble(Constants.QUANTITY)!!.toInt(),
       prod.getDate(Constants.DATE)!!,
       prod.getDouble(Constants.PRICE)!!,
       prod.getBoolean(Constants.IS_DOLAR)!!,
       prod.getDouble(Constants.RATE)!!
      )
      productions.add(productionNew)
     }
     trySend(productions)
    }
   awaitClose { request.remove() }
  }
 }
}