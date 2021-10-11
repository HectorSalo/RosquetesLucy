package com.skysam.hchirinos.rosqueteslucy.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skysam.hchirinos.rosqueteslucy.database.SharedPref

/**
 * Created by Hector Chirinos (Home) on 15/9/2021.
 */
class SettingsViewModel: ViewModel() {
    private val _lockActived = MutableLiveData<Boolean>().apply {
        value = SharedPref.isLock()
    }
    val lockActived: LiveData<Boolean> get() = _lockActived

    fun changeLockState(isLock: Boolean) {
        _lockActived.value = isLock
        if (!isLock) SharedPref.changeLock(false)
    }
}