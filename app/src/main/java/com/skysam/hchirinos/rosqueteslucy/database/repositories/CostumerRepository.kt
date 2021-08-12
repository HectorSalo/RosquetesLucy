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
            Constants.COSTUMER_IDENTIFIER to costumer.identifier
        )
        getInstance().add(data)
            .addOnSuccessListener {
                val dataLocation = hashMapOf(
                    Constants.COSTUMER_LOCATION to costumer.locations[0]
                )
                getInstance().document(it.id).collection(Constants.LOCATIONS)
                    .add(dataLocation)
            }
    }

    fun editCostumer(costumer: Costumer) {
        val data: Map<String, Any> = hashMapOf(
            Constants.NAME to costumer.name,
            Constants.COSTUMER_IDENTIFIER to costumer.identifier
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
                        val locations = mutableListOf<String>()
                        getInstance().document(doc.id).collection(Constants.LOCATIONS)
                            .get()
                            .addOnSuccessListener {
                                for (document in it) {
                                    locations.add(document.getString(Constants.COSTUMER_LOCATION)!!)
                                }
                            }
                        val costumer = Costumer(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            doc.getString(Constants.COSTUMER_IDENTIFIER)!!,
                            locations
                        )
                        costumers.add(costumer)
                    }
                    offer(costumers)
                }
            awaitClose { request.remove() }
        }
    }

    fun addLocation(id: String, location: String) {
        val dataLocation = hashMapOf(
            Constants.COSTUMER_LOCATION to location
        )
        getInstance().document(id).collection(Constants.LOCATIONS)
            .add(dataLocation)
    }

    fun deleteLocations(id: String, locations: MutableList<String>) {
        for (loc in locations) {
            getInstance().document(id).collection(Constants.LOCATIONS)
                .whereEqualTo(Constants.COSTUMER_LOCATION, loc)
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        getInstance().document(id).collection(Constants.LOCATIONS)
                            .document(doc.id)
                            .delete()
                    }
                }
        }
    }

    fun deleteCostumer(costumer: Costumer) {
        for (loc in costumer.locations) {
            getInstance().document(costumer.id).collection(Constants.LOCATIONS)
                .whereEqualTo(Constants.COSTUMER_LOCATION, loc)
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        getInstance().document(costumer.id).collection(Constants.LOCATIONS)
                            .document(doc.id)
                            .delete()
                            .addOnSuccessListener {
                                if (loc == costumer.locations.last()) {
                                    getInstance().document(costumer.id)
                                        .delete()
                                }
                            }
                    }
                }
        }
    }
}