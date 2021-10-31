package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.*
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Customer
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

    fun addCostumer(customer: Customer) {
        val data = hashMapOf(
            Constants.NAME to customer.name,
            Constants.COSTUMER_IDENTIFIER to customer.identifier,
            Constants.COSTUMER_ADDRESS to customer.address,
            Constants.LOCATIONS to customer.locations
        )
        getInstance().add(data)
    }

    fun editCostumer(customer: Customer) {
        val data: Map<String, Any> = hashMapOf(
            Constants.NAME to customer.name,
            Constants.COSTUMER_IDENTIFIER to customer.identifier,
            Constants.COSTUMER_ADDRESS to customer.address,
            Constants.LOCATIONS to customer.locations
        )
        getInstance().document(customer.id)
            .update(data)
    }

    fun getCostumers(): Flow<MutableList<Customer>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.NAME, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val customers: MutableList<Customer> = mutableListOf()
                    for (doc in value!!) {
                        var listLocation = mutableListOf<String>()
                        if (doc.get(Constants.LOCATIONS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            listLocation = doc.get(Constants.LOCATIONS) as MutableList<String>
                        }
                        val costumer = Customer(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            doc.getString(Constants.COSTUMER_IDENTIFIER)!!,
                            doc.getString(Constants.COSTUMER_ADDRESS)!!,
                            listLocation
                        )
                        customers.add(costumer)
                    }
                    offer(customers)
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

    fun deleteCostumer(customer: Customer) {
        getInstance().document(customer.id)
            .delete()
    }
}