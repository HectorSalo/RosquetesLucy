package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Location
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos (Home) on 2/8/2021.
 */
object CostumerRepository {
    fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.COSTUMERS)
    }

    fun getInstanceLocations(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.LOCATIONS)
    }

    fun addCostumer(costumer: Costumer) {
        val data = hashMapOf(
            Constants.NAME to costumer.name,
            Constants.COSTUMER_IDENTIFIER to costumer.identifier
        )
        getInstance().add(data)
            .addOnSuccessListener {
                val dataLocation = hashMapOf(
                    Constants.COSTUMER_LOCATION to costumer.locations[0],
                    Constants.ID_COSTUMER to it.id
                )
                getInstanceLocations()
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

                    val locations = mutableListOf<Location>()
                    getInstanceLocations()
                        .get()
                        .addOnSuccessListener {
                            for (document in it) {
                                val location = Location(
                                    document.id,
                                    document.getString(Constants.COSTUMER_LOCATION)!!,
                                    document.getString(Constants.ID_COSTUMER)!!
                                )
                                locations.add(location)
                            }

                            val costumers: MutableList<Costumer> = mutableListOf()
                            for (doc in value!!) {
                                val listLocation = mutableListOf<Location>()
                                for (i in locations.indices) {
                                    if (doc.id == locations[i].idCostumer) {
                                        listLocation.add(locations[i])
                                    }
                                }
                                val costumer = Costumer(
                                    doc.id,
                                    doc.getString(Constants.NAME)!!,
                                    doc.getString(Constants.COSTUMER_IDENTIFIER)!!,
                                    listLocation
                                )
                                costumers.add(costumer)
                            }
                            offer(costumers)
                        }
                }
            awaitClose { request.remove() }
        }
    }

    fun addLocation(id: String, location: String) {
        val dataLocation = hashMapOf(
            Constants.COSTUMER_LOCATION to location,
            Constants.ID_COSTUMER to id
        )
        getInstanceLocations()
            .add(dataLocation)
    }

    fun deleteLocations(locations: MutableList<Location>) {
        for (loc in locations) {
            getInstanceLocations()
                .document(loc.id)
                .delete()
        }
    }

    fun deleteCostumer(costumer: Costumer) {
        for (loc in costumer.locations) {
            getInstanceLocations()
                .document(loc.id)
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