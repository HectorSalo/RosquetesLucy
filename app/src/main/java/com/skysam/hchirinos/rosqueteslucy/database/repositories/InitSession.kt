package com.skysam.hchirinos.rosqueteslucy.database.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos (Home) on 14/9/2021.
 */
object InitSession {
    private fun getInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    fun getCurrentUser(): FirebaseUser? {
        return getInstance().currentUser
    }

    fun initSession(email: String, password: String): Task<AuthResult> {
        return getInstance().signInWithEmailAndPassword(email, password)
    }
}