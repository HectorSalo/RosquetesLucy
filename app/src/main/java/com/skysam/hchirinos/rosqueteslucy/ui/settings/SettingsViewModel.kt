package com.skysam.hchirinos.rosqueteslucy.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skysam.hchirinos.rosqueteslucy.common.CloudMessaging
import com.skysam.hchirinos.rosqueteslucy.database.SharedPref

/**
 * Created by Hector Chirinos (Home) on 15/9/2021.
 */
class SettingsViewModel: ViewModel() {
    private val _lockActived = MutableLiveData<Boolean>().apply {
        value = SharedPref.isLock()
    }
    val lockActived: LiveData<Boolean> get() = _lockActived

    private val _notificationActived = MutableLiveData<Boolean>().apply {
        value = SharedPref.isNotificationActive()
    }
    val notificationActived: LiveData<Boolean> get() = _notificationActived

    private val _notificationUpdatesActive = MutableLiveData<Boolean>().apply {
        value = SharedPref.isNotificationUpdatesActive()
    }
    val notificationUpdatesActived: LiveData<Boolean> get() = _notificationUpdatesActive

    private val _notificationSalePaidActived = MutableLiveData<Boolean>().apply {
        value = SharedPref.isNotificationSalePaidActive()
    }
    val notificationSalePaidActived: LiveData<Boolean> get() = _notificationSalePaidActived

    private val _notificationNoteSalePaidActived = MutableLiveData<Boolean>().apply {
        value = SharedPref.isNotificationNoteSalePaidActive()
    }
    val notificationNoteSalePaidActived: LiveData<Boolean> get() = _notificationNoteSalePaidActived

    fun changeLockState(isLock: Boolean) {
        _lockActived.value = isLock
        if (!isLock) SharedPref.changeLock(false)
    }

    fun changeNotificationState(isActive: Boolean) {
        _notificationActived.value = isActive
        _notificationUpdatesActive.value = isActive
        _notificationSalePaidActived.value = isActive
        _notificationNoteSalePaidActived.value = isActive
        SharedPref.changeNotificationActive(isActive)
        SharedPref.changeNotificationUpdatesActive(isActive)
        SharedPref.changeNotificationSalePaidActive(isActive)
        SharedPref.changeNotificationNoteSalePaidActive(isActive)
        if (isActive) {
            CloudMessaging.subscribeToTopicUpdateApp()
            CloudMessaging.subscribeToTopicSalePaid()
            CloudMessaging.subscribeToTopicNoteSalePaid()
        } else {
            CloudMessaging.unsubscribeToTopicUpdateApp()
            CloudMessaging.unsubscribeToTopicSalePaid()
            CloudMessaging.unsubscribeToTopicNoteSalePaid()
        }
    }

    fun changeNotificationUpdateState(isActive: Boolean) {
        _notificationUpdatesActive.value = isActive
        SharedPref.changeNotificationUpdatesActive(isActive)
        if (isActive) CloudMessaging.subscribeToTopicUpdateApp()
        else CloudMessaging.unsubscribeToTopicUpdateApp()
    }

    fun changeNotificationSalePaidState(isActive: Boolean) {
        _notificationSalePaidActived.value = isActive
        SharedPref.changeNotificationSalePaidActive(isActive)
        if (isActive) CloudMessaging.subscribeToTopicSalePaid()
        else CloudMessaging.unsubscribeToTopicSalePaid()
    }

    fun changeNotificationNoteSalePaidState(isActive: Boolean) {
        _notificationNoteSalePaidActived.value = isActive
        SharedPref.changeNotificationNoteSalePaidActive(isActive)
        if (isActive) CloudMessaging.subscribeToTopicNoteSalePaid()
        else CloudMessaging.unsubscribeToTopicNoteSalePaid()
    }
}