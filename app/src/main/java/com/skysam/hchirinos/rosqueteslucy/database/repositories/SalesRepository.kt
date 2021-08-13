package com.skysam.hchirinos.rosqueteslucy.database.repositories

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
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
            Constants.COSTUMER_LOCATION to sale.idLocation,
            Constants.PRICE to sale.price,
            Constants.QUANTITY to sale.quantity,
            Constants.IS_DOLAR to sale.isDolar,
            Constants.NUMBER_INVOICE to sale.invoice,
            Constants.IS_PAID to sale.isPaid,
            Constants.DATE_DELIVERY to Date(sale.date)
        )
        getInstance().add(data)
    }
}