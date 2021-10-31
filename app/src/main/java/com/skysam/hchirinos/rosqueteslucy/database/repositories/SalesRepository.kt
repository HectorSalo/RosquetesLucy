package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 3/8/2021.
 */
object SalesRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.SALES)
    }

    fun getValueWeb(): Flow<String> {
        return callbackFlow {
            var valor: String? = null
            val url = "http://www.bcv.org.ve/"

            withContext(Dispatchers.IO) {
               try {
                   val doc = Jsoup.connect(url).get()
                   val data = doc.select("div#dolar")
                   valor = data.select("strong").last()?.text()
                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                }
            }
            if (valor != null) {
                val valorNeto = valor?.replace(",", ".")
                val valorCotizacion = valorNeto!!.toFloat()
                val valorRounded = String.format(Locale.US, "%.2f", valorCotizacion)
                offer(valorRounded)
            } else {
                offer("1,00")
            }

            awaitClose { }
        }
    }

    fun addSale(sale: Sale) {
        val data = hashMapOf(
            Constants.ID_COSTUMER to sale.idCostumer,
            Constants.NAME to sale.nameCostumer,
            Constants.COSTUMER_LOCATION to sale.location,
            Constants.PRICE to sale.price,
            Constants.RATE_DELIVERY to sale.rateDelivery,
            Constants.RATE_PAID to sale.ratePaid,
            Constants.QUANTITY to sale.quantity,
            Constants.IS_DOLAR to sale.isDolar,
            Constants.NUMBER_INVOICE to sale.invoice,
            Constants.IS_PAID to sale.isPaid,
            Constants.DATE_DELIVERY to Date(sale.dateDelivery),
            Constants.DATE_PAID to Date(sale.datePaid),
            Constants.IS_ANULLED to sale.isAnnuled
        )
        getInstance().add(data)
    }

    fun getSales(): Flow<MutableList<Sale>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.NUMBER_INVOICE, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val sales = mutableListOf<Sale>()
                    for (sale in value!!) {
                        var isAnulled = false
                        if (sale.getBoolean(Constants.IS_ANULLED) != null) {
                            isAnulled = sale.getBoolean(Constants.IS_ANULLED)!!
                        }
                        val saleNew = Sale(
                            sale.id,
                            sale.getString(Constants.ID_COSTUMER)!!,
                            sale.getString(Constants.NAME)!!,
                            sale.getString(Constants.COSTUMER_LOCATION)!!,
                            sale.getDouble(Constants.PRICE)!!,
                            sale.getDouble(Constants.RATE_DELIVERY)!!,
                            sale.getDouble(Constants.RATE_PAID)!!,
                            sale.getDouble(Constants.QUANTITY)!!.toInt(),
                            sale.getBoolean(Constants.IS_DOLAR)!!,
                            sale.getDouble(Constants.NUMBER_INVOICE)!!.toInt(),
                            sale.getBoolean(Constants.IS_PAID)!!,
                            sale.getDate(Constants.DATE_DELIVERY)!!.time,
                            sale.getDate(Constants.DATE_PAID)!!.time,
                            isAnulled
                        )
                        sales.add(saleNew)
                    }
                    offer(sales)
                }
            awaitClose { request.remove() }
        }
    }

    fun annulSale(sale: Sale) {
        val data: Map<String, Any> = hashMapOf(
            Constants.IS_PAID to true,
            Constants.DATE_PAID to Date(),
            Constants.IS_ANULLED to true
        )
        getInstance().document(sale.id)
            .update(data)
    }

    fun paidSale(sale: Sale) {
        val data: Map<String, Any> = hashMapOf(
            Constants.IS_PAID to true,
            Constants.DATE_PAID to Date(sale.datePaid),
            Constants.RATE_PAID to sale.ratePaid
        )
        getInstance().document(sale.id)
            .update(data)
    }

    fun deleteSale(sale: Sale) {
        getInstance().document(sale.id)
            .delete()
    }

    fun editSale(sale: Sale) {
        val data: Map<String, Any> = hashMapOf(
            Constants.ID_COSTUMER to sale.idCostumer,
            Constants.NAME to sale.nameCostumer,
            Constants.COSTUMER_LOCATION to sale.location,
            Constants.PRICE to sale.price,
            Constants.RATE_DELIVERY to sale.rateDelivery,
            Constants.RATE_PAID to sale.ratePaid,
            Constants.QUANTITY to sale.quantity,
            Constants.IS_DOLAR to sale.isDolar,
            Constants.NUMBER_INVOICE to sale.invoice,
            Constants.IS_PAID to sale.isPaid,
            Constants.DATE_DELIVERY to Date(sale.dateDelivery),
            Constants.DATE_PAID to Date(sale.datePaid),
            Constants.IS_ANULLED to sale.isAnnuled
        )
        getInstance().document(sale.id)
            .update(data)
    }
}