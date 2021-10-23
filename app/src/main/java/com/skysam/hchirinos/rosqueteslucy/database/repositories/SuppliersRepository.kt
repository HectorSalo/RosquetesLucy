package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Supplier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos (Home) on 30/9/2021.
 */
object SuppliersRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.SUPPLIERS)
    }

    fun addSupplier(supplier: Supplier) {
        val data = hashMapOf(
            Constants.NAME to supplier.name,
            Constants.LOCATIONS to supplier.locations
        )
        getInstance().add(data)
    }

    fun getSuppliers(): Flow<MutableList<Supplier>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.NAME, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val suppliers: MutableList<Supplier> = mutableListOf()
                    for (doc in value!!) {
                        var listLocation = mutableListOf<String>()
                        if (doc.get(Constants.LOCATIONS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            listLocation = doc.get(Constants.LOCATIONS) as MutableList<String>
                        }
                        val supplier = Supplier(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            listLocation
                        )
                        suppliers.add(supplier)
                    }
                    offer(suppliers)
                }
            awaitClose { request.remove() }
        }
    }

    fun updateSupplier(supplier: Supplier) {
        val data = hashMapOf(
            Constants.NAME to supplier.name,
            Constants.LOCATIONS to supplier.locations
        )
        getInstance().document(supplier.id)
            .update(data)
    }

    fun deleteSupplier(supplier: Supplier) {
        getInstance().document(supplier.id)
            .delete()
    }
}