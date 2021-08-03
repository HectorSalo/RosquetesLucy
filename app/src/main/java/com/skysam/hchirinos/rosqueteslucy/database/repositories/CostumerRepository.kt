package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos (Home) on 2/8/2021.
 */
object CostumerRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.COSTUMERS)
    }

    fun addCostumer(costumer: Costumer) {
        val data = hashMapOf(
            Constants.NAME to costumer.name,
            Constants.COSTUMER_IDENTIFIER to costumer.identifier,
            Constants.COSTUMER_LOCATION to costumer.location
        )
        getInstance().add(data)
    }

    fun editCostumer(costumer: Costumer) {

    }

    fun deleteCostumer(id: String) {
        getInstance().document(id).delete()
    }

    fun getCostumers(): Flow<MutableList<Costumer>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.NAME, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val costumers: MutableList<Costumer> = mutableListOf()
                    for (doc in value!!) {
                        val costumer = Costumer(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            doc.getString(Constants.COSTUMER_IDENTIFIER)!!,
                            doc.getString(Constants.COSTUMER_LOCATION)!!
                        )
                        costumers.add(costumer)
                    }
                    offer(costumers)
                }
            awaitClose { request.remove() }
        }
    }
}