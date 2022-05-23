package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.PrimaryProducts
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos (Home) on 1/10/2021.
 */
object PrimaryProductsRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.LIST_EXPENSES)
    }

    fun addFirstPrimaryProduct(primaryProducts: PrimaryProducts) {
        val list = mutableListOf<String>()
        list.add(primaryProducts.name)
        val data = hashMapOf(
            Constants.ID_SUPPLIER to Constants.PRIMARY_PRODUCTS,
            Constants.PRIMARY_PRODUCTS to list
        )
        getInstance()
            .document(Constants.PRIMARY_PRODUCTS)
            .set(data)
    }

    fun addPrimaryProduct(primaryProducts: PrimaryProducts) {
        getInstance().document(Constants.PRIMARY_PRODUCTS)
            .update(Constants.PRIMARY_PRODUCTS, FieldValue.arrayUnion(primaryProducts.name))
    }

    fun getPrimaryProducts(): Flow<MutableList<String>> {
        return callbackFlow {
            val request = getInstance()
                .whereEqualTo(Constants.ID_SUPPLIER, Constants.PRIMARY_PRODUCTS)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    var products = mutableListOf<String>()
                    for (doc in value!!) {
                        if (doc.get(Constants.PRIMARY_PRODUCTS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            products = doc.get(Constants.PRIMARY_PRODUCTS) as MutableList<String>
                        }
                    }
                    trySend(products)
                }
            awaitClose { request.remove() }
        }
    }
}