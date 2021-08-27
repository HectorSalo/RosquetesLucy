package com.skysam.hchirinos.rosqueteslucy.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos on 27/08/2021.
 */
object ExpensesRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.EXPENSES)
    }

    fun addExpense(expense: Expense) {
        val data = hashMapOf(
            Constants.NAME to expense.name,
            Constants.PRICE to expense.price,
            Constants.RATE_PAID to expense.rate,
            Constants.QUANTITY to expense.quantity,
            Constants.IS_DOLAR to expense.isDolar,
            Constants.DATE_PAID to Date(expense.date)
        )
        getInstance().add(data)
    }

    fun getExpenses(): Flow<MutableList<Expense>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.DATE_PAID, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val expenses = mutableListOf<Expense>()
                    for (expense in value!!) {
                        val expenseNew = Expense(
                            expense.id,
                            expense.getString(Constants.NAME)!!,
                            expense.getDouble(Constants.PRICE)!!,
                            expense.getDouble(Constants.RATE_PAID)!!,
                            expense.getDouble(Constants.QUANTITY)!!,
                            expense.getBoolean(Constants.IS_DOLAR)!!,
                            expense.getDate(Constants.DATE_PAID)!!.time
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

    fun editExpense(expense: Expense) {
        val data: Map<String, Any> = hashMapOf(
            Constants.NAME to expense.name,
            Constants.PRICE to expense.price,
            Constants.RATE_PAID to expense.rate,
            Constants.QUANTITY to expense.quantity,
            Constants.IS_DOLAR to expense.isDolar,
            Constants.DATE_PAID to Date(expense.date)
        )
        getInstance().document(expense.id)
            .update(data)
    }
}