package com.skysam.hchirinos.rosqueteslucy.ui.initSession

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skysam.hchirinos.rosqueteslucy.database.SharedPref
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
                    _messageSession.value = task.exception?.localizedMessage
                }
            }
    }

    private val _numberPass = MutableLiveData<String>().apply { value = "" }
    val numberPass: LiveData<String> get() = _numberPass

    private val _passAccept = MutableLiveData<Boolean>()
    val passAccept: LiveData<Boolean> get() = _passAccept

    fun addNewNumber(number: Int) {
        if (_numberPass.value!!.length < 4) {
            val newString = "${_numberPass.value}$number"
            _numberPass.value = newString
            if (_numberPass.value!!.length == 4) {
                _passAccept.value = _numberPass.value!! == SharedPref.getPinLock()
            }
        }
    }

    fun deleteNumber() {
        if (_numberPass.value!!.isNotEmpty()) {
            _numberPass.value = _numberPass.value?.substring(0, _numberPass.value!!.length - 1)
        }
    }
}