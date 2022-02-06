package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.ExpenseOld
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.PrimaryProducts
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by Hector Chirinos on 27/08/2021.
 */
object ExpensesRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.LIST_EXPENSES)
    }

    fun addExpense(expense: Expense) {
        val data = hashMapOf(
            Constants.NAME to expense.nameSupplier,
            Constants.ID_SUPPLIER to expense.idSupplier,
            Constants.PRIMARY_PRODUCTS to expense.listProducts,
            Constants.PRICE to expense.total,
            Constants.DATE_PAID to Date(expense.dateCreated!!),
            Constants.RATE_PAID to expense.rate
        )
        getInstance().add(data)
    }

    fun getExpenses(): Flow<MutableList<Expense>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.DATE_PAID, Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val expenses = mutableListOf<Expense>()
                    for (expense in value!!) {
                        val listProducts = mutableListOf<PrimaryProducts>()
                        if (expense.get(Constants.PRIMARY_PRODUCTS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            val products = expense.data.getValue(Constants.PRIMARY_PRODUCTS) as ArrayList<HashMap<String, Any>>
                            for (prod in products) {
                                val product = PrimaryProducts(
                                    prod[Constants.NAME].toString(),
                                    prod[Constants.UNIT].toString(),
                                    prod[Constants.PRICE].toString().toDouble(),
                                    prod[Constants.QUANTITY].toString().toDouble()
                                )
                                listProducts.add(product)
                            }
                        }
                        val expenseNew = Expense(
                            expense.id,
                            expense.getString(Constants.NAME)!!,
                            expense.getString(Constants.ID_SUPPLIER)!!,
                            listProducts,
                            expense.getDouble(Constants.PRICE)!!,
                            expense.getDate(Constants.DATE_PAID)!!.time,
                            expense.getDouble(Constants.RATE_PAID)!!
                        )
                        expenses.add(expenseNew)
                    }
                    offer(expenses)
                }
            awaitClose { request.remove() }
        }
    }

    fun deleteExpense(expense: Expense) {
        getInstance().document(expense.id)
            .delete()
    }

    fun editExpense(expenseOld: ExpenseOld) {
        val data: Map<String, Any> = hashMapOf(
            Constants.NAME to expenseOld.name,
            Constants.PRICE to expenseOld.price,
            Constants.RATE_PAID to expenseOld.rate,
            Constants.QUANTITY to expenseOld.quantity,
            Constants.IS_DOLAR to expenseOld.isDolar,
            Constants.DATE_PAID to Date(expenseOld.date)
        )
        getInstance().document(expenseOld.id)
            .update(data)
    }
}