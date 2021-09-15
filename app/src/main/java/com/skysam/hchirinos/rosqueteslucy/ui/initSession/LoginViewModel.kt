package com.skysam.hchirinos.rosqueteslucy.ui.initSession

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skysam.hchirinos.rosqueteslucy.database.repositories.InitSession

/**
 * Created by Hector Chirinos (Home) on 14/9/2021.
 */
class LoginViewModel: ViewModel() {
    private val _messageSession = MutableLiveData<String>()
    val messageSession: LiveData<String> get() = _messageSession

    fun initSession(email: String, password: String) {
        InitSession.initSession(email, password)
            .addOnCompleteListener {task->
                if (task.isSuccessful) {
                    _messageSession.value = "ok"
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    _messageSession.value = task.exception?.message
                }
            }
    }
}