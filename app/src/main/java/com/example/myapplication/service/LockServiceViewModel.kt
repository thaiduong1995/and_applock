package com.example.myapplication.service

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockServiceViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    var listLockedAppLiveData: LiveData<List<AppData>>? = null
    var locationLocksLiveData: LiveData<List<LocationLock>>? = null
    var timeLocksLiveData: LiveData<List<TimeItem>>? = null
    var groupWifiLiveData: LiveData<List<GroupWifi>>? = null
    var wifiLiveData: LiveData<List<ItemWifi>>? = null

    init {
        listLockedAppLiveData =
            AppDatabase.getInstance(context).getLockedAppsDao().getLockedApps()
        locationLocksLiveData = AppDatabase.getInstance(context).getLocationDao().getAllLocations()
        timeLocksLiveData = AppDatabase.getInstance(context).getTimeLockDao().getTimeLockAsyn()
        groupWifiLiveData =
            AppDatabase.getInstance(context).getGroupWifiDao().getAllGroupWifisAsyn()
        wifiLiveData = AppDatabase.getInstance(context).getWfiDao().getAllWifisAsync()
    }

    fun isAppLocked(
        appLockerService: AppLockerService,
        listLockedApp: List<AppData>,
        packageName: String
    ): Boolean {
        return if (packageName == appLockerService.packageName) {
            false
        } else {
            listLockedApp.find { it.packageName == packageName } != null
        }
    }

    fun isCanLockApp(listLockedApp: List<AppData>, packageName: String): Boolean {
        return listLockedApp.find { it.packageName == packageName }?.isCanLock == true
    }

    fun removeAppData(appLockerService: AppLockerService, pkgName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance(appLockerService).getLockedAppsDao()
                .unlockApp(pkgName)
        }
    }

    fun lockApp(appData: AppData) {
        viewModelScope.launch(Dispatchers.IO) {
            appData.isLock = true
            AppDatabase.getInstance(context).getLockedAppsDao().lockApp(appData)
            Log.d("thinhvh", "autoLockApp : ${appData.packageName}")
        }
    }
}