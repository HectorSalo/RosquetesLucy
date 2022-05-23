package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.*
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
            Constants.COSTUMER_ADDRESS to costumer.address,
            Constants.LOCATIONS to costumer.locations
        )
        getInstance().add(data)
    }

    fun editCostumer(costumer: Costumer) {
        val data: Map<String, Any> = hashMapOf(
            Constants.NAME to costumer.name,
            Constants.COSTUMER_IDENTIFIER to costumer.identifier,
            Constants.COSTUMER_ADDRESS to costumer.address,
            Constants.LOCATIONS to costumer.locations
        )
        getInstance().document(costumer.id)
            .update(data)
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
                        var listLocation = mutableListOf<String>()
                        if (doc.get(Constants.LOCATIONS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            listLocation = doc.get(Constants.LOCATIONS) as MutableList<String>
                        }
                        val costumer = Costumer(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            doc.getString(Constants.COSTUMER_IDENTIFIER)!!,
                            doc.getString(Constants.COSTUMER_ADDRESS)!!,
                            listLocation
                        )
                        costumers.add(costumer)
                    }
                    trySend(costumers)
                }
            awaitClose { request.remove() }
        }
    }

    fun addLocation(id: String, location: String) {
        getInstance().document(id)
            .update(Constants.LOCATIONS, FieldValue.arrayUnion(location))
    }

    fun deleteLocations(id: String, locations: MutableList<String>) {
        for (loc in locations) {
            getInstance().document(id)
                .update(Constants.LOCATIONS, FieldValue.arrayRemove(loc))
        }
    }

    fun deleteCostumer(costumer: Costumer) {
        getInstance().document(costumer.id)
            .delete()
    }
}