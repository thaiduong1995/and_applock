package com.example.myapplication.view_model

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.model.*
import com.example.myapplication.data.model.liveData.MutableStateLiveData
import com.example.myapplication.data.repository.Repository
import com.example.myapplication.ui.fragment.purchase.PurchaseModel
import com.example.myapplication.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Thinhvh on 23/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mainRepository: Repository,
    private val networkHelper: NetworkHelper,
    private val preference: PreferenceHelper
) : BaseViewModel() {

    //permission
    var readExternalStoragePermissionLiveData = MutableLiveData(false)
    var cameraPermissionLiveData = MutableLiveData(false)
    var locationPermissionLiveData = MutableLiveData(false)
    var contactPermissionLiveData = MutableLiveData(false)
    var searchLocation = MutableLiveData<RecentSearch?>()
    var purchaseLiveData = MutableStateLiveData<List<PurchaseModel>>()

    var listLockedAppLiveData = MutableStateLiveData<List<AppData>>()
    var listSuggestAppLiveData = MutableStateLiveData<List<AppData>>()
    var listSuggestSearchLiveData = MutableStateLiveData<List<AppData>?>()
    var listLockConditionLiveData = MutableStateLiveData<List<LockCondition>>()
    private val listApp = arrayListOf<AppData>()
    private val listSuggestApp = arrayListOf<AppData>()
    var filterType = Filter.Locked

    var regexScreenLiveData = MutableLiveData<RegexModel?>()

    fun setRegexScreen(regexModel: RegexModel) {
        viewModelScope.launch(Dispatchers.IO) {
            regexScreenLiveData.postValue(regexModel)
        }
    }

    fun setTimeUnLock() {
        preference.setTimeUnLock(0)
    }

    fun isSetupPassword(): Boolean {
        return preference.getPatternCode().isNotEmpty() || preference.getKnockCode()
            .isNotEmpty() || preference.getPinCode().isNotEmpty()
    }

    fun isSetupPatternCode(): Boolean {
        return preference.getPatternCode().isNotEmpty()
    }

    fun isSetupKnockCode(): Boolean {
        return preference.getKnockCode().isNotEmpty()
    }

    fun isSetupPinCode(): Boolean {
        return preference.getPinCode().isNotEmpty()
    }

    fun isHidePatternTrails(): Boolean {
        return preference.isHidePatternTrails()
    }

    fun savePassword(currentType: Password?, password: String) {
        when (currentType?.id) {
            Constants.TYPE_PIN_4_digit, Constants.TYPE_PIN_6_digit -> {
                preference.setPinCode(password)
            }

            Constants.TYPE_KNOCK_CODE -> {
                preference.setKnockCode(password)
            }

            Constants.TYPE_PATTERN -> {
                preference.setPatternCode(password)
            }
        }
    }

    fun saveLockType(currentType: Password?) {
        when (currentType?.id) {
            Constants.TYPE_PIN_4_digit, Constants.TYPE_PIN_6_digit -> {
                preference.setLockType(OverlayValidateType.TYPE_PIN.value)
            }

            Constants.TYPE_KNOCK_CODE -> {
                preference.setLockType(OverlayValidateType.TYPE_KNOCK_CODE.value)
            }

            Constants.TYPE_PATTERN -> {
                preference.setLockType(OverlayValidateType.TYPE_PATTERN.value)
            }
        }
    }

//    fun getListConditionLock() {
//        Log.d("thinhvh", "getListConditionLock: ")
//        listLockConditionLiveData.postLoading()
//        viewModelScope.launch(Dispatchers.IO) {
//            val listConditionLock = ArrayList<LockCondition>()
//            val wifiEnable =
//                AppDatabase.getInstance(context).getGroupWifiDao().getAllGroupWifiSync()
//                    .filter { it.enabled }.isNotEmpty()
//            val timeEnable =
//                AppDatabase.getInstance(context).getTimeLockDao().getTimeLock().filter { it.enable }
//                    .isNotEmpty()
//            val locationEnable =
//                AppDatabase.getInstance(context).getLocationDao().getLocationsSync()
//                    .filter { it.enabled }.isNotEmpty()
//
//            if (wifiEnable) {
//                listConditionLock.add(LockCondition.WIFI_LOCK)
//            }
//            if (timeEnable) {
//                listConditionLock.add(LockCondition.TIME_LOCK)
//            }
//            if (locationEnable) {
//                listConditionLock.add(LockCondition.LOCATION_LOCK)
//            }
//            listLockConditionLiveData.postSuccess(listConditionLock)
//        }
//    }

    fun changeSateLockAllApp(context: Context, isLock: Boolean) {
        listLockedAppLiveData.postLoading()
        listLockedAppLiveData.value?.getData()?.let {
            it.onEach {
                it.isLock = isLock
                lockOrUnLockApp(context, it)
            }
            listLockedAppLiveData.postSuccess(it)
        }
    }

    fun getListInstallApp() {
        listLockedAppLiveData.postLoading()
        viewModelScope.launch(Dispatchers.IO) {
            val listInstallApp = mainRepository.fetchInstallApp(context)
            val listLockedApp = mainRepository.fetchLockedApp(context)

            listInstallApp.onEach {
                it.isLock = listLockedApp.find { lockedApp ->
                    it.packageName.lowercase() == lockedApp.packageName.lowercase()
                } != null
            }
            listApp.clear()
            listApp.addAll(listInstallApp)
            listLockedAppLiveData.postSuccess(sortList(listInstallApp))
        }
    }

    fun getListSystemApp() {
        val listSystemApp = listApp.filter { it.systemApp }
        listLockedAppLiveData.postLoading()
        listLockedAppLiveData.postSuccess(sortList(ArrayList(listSystemApp)))
    }

    fun getNewInstallApp() {
        val listSystemApp = listApp.filter { it.isNewInstall }
        listLockedAppLiveData.postLoading()
        listLockedAppLiveData.postSuccess(sortList(ArrayList(listSystemApp)))
    }

    fun searchApp(keyWordSearch: String) {
        listLockedAppLiveData.postLoading()
        if (keyWordSearch.isEmpty()) {
            listLockedAppLiveData.postSuccess(listApp)
        } else {
            listLockedAppLiveData.postSuccess(listApp.filter {
                it.appName.lowercase().contains(keyWordSearch.lowercase())
            })
        }
    }

    fun changeFilter(type: Filter) {
        this.filterType = type
        val currentValue = listLockedAppLiveData.value?.getData()?.let { ArrayList(it) }
        listLockedAppLiveData.postLoading()
        currentValue?.let {
            listLockedAppLiveData.postSuccess(sortList(it))
        }
    }

    private fun sortList(currentValue: ArrayList<AppData>): List<AppData> {
        when (filterType) {
            Filter.Locked -> {
                currentValue.sortByDescending { it.isLock }
            }

            Filter.UnLocked -> {
                currentValue.sortBy { it.isLock }
            }

            Filter.AZ -> {
                currentValue.sortBy { it.appName.lowercase() }
            }

            Filter.ZA -> {
                currentValue.sortByDescending { it.appName.lowercase() }
            }

            Filter.Newest -> {
                currentValue.sortByDescending { it.timeInstall }
            }

            Filter.Oldest -> {
                currentValue.sortBy { it.timeInstall }
            }
        }
        return currentValue
    }

    fun lockOrUnLockApp(context: Context, appData: AppData) {
        viewModelScope.launch(Dispatchers.IO) {
            if (appData.isLock) {
                AppDatabase.getInstance(context).getLockedAppsDao().lockApp(appData)
            } else {
                AppDatabase.getInstance(context).getLockedAppsDao().unlockApp(appData.packageName)
            }
        }
    }

    fun lockApps(context: Context, listApp: ArrayList<AppData>) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance(context).getLockedAppsDao().lockApps(listApp)
        }
    }

    fun getSuggestApp(context: Context) {
        listSuggestAppLiveData.postLoading()
        viewModelScope.launch {
            val data = arrayListOf<AppData>()
            Constants.listReCommentApp.forEach {
                try {
                    val appInfo = Utils.getAppInfo(context, it)
                    appInfo?.let { item ->
                        data.add(item)
                    }
                } catch (e: java.lang.Exception) {
                    listSuggestAppLiveData.postError(e.message)
                }
            }
            if (data.size < 10) {
                val listInstallApp = mainRepository.fetchInstallApp(context)
                listInstallApp.filter { !data.contains(it) }.let {
                    if (listInstallApp.size > 10) {
                        data.addAll(it.subList(0, 10))
                    } else {
                        data.addAll(it)
                    }
                }
            }
            listSuggestApp.clear()
            listSuggestApp.addAll(data)
            listSuggestAppLiveData.postSuccess(listSuggestApp)
        }
    }

    fun searchSuggestApp(keyWordSearch: String) {
        listSuggestSearchLiveData.postLoading()
        viewModelScope.launch {
            if (keyWordSearch.isEmpty()) {
                listSuggestSearchLiveData.postSuccess(null)
            }
            listApp.filter { !listSuggestApp.contains(it) }
                .filter { it.appName.lowercase().contains(keyWordSearch.lowercase()) }.let {
                    listSuggestSearchLiveData.postSuccess(it)
                }
        }
    }

    fun onPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (permissions.isEmpty()) {
            return
        }
        when (requestCode) {
            PermissionChecker.REQUEST_CODE_READ_EXTERNAL_STORAGE -> {
                if (permissions.first() == android.Manifest.permission.READ_EXTERNAL_STORAGE && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    readExternalStoragePermissionLiveData.postValue(true)
                }
            }

            PermissionChecker.REQUEST_CODE_CAMERA -> {
                if (permissions.first() == android.Manifest.permission.CAMERA && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    cameraPermissionLiveData.postValue(true)
                }
            }

            PermissionChecker.REQUEST_CODE_LOCATION -> {
                if (permissions.first() == android.Manifest.permission.ACCESS_FINE_LOCATION && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionLiveData.postValue(true)
                }
            }

            PermissionChecker.REQUEST_CODE_CONTACT -> {
                if (permissions.first() == android.Manifest.permission.WRITE_CONTACTS && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    contactPermissionLiveData.postValue(true)
                }
            }
        }
    }

    fun setCurrentSearchLocation(search: RecentSearch?) {
        searchLocation.value = search
    }

    fun getPurchase() {
        purchaseLiveData.postLoading()
        viewModelScope.launch(Dispatchers.IO) {
            val listItems = ArrayList<PurchaseModel>()
            val purchaseMonth = PurchaseModel.PurchaseMonthModel()
            val purchaseWeek = PurchaseModel.PurchaseWeekModel()
            val purchaseYear = PurchaseModel.PurchaseYearModel()
            listItems.add(purchaseMonth)
            listItems.add(purchaseWeek)
            listItems.add(purchaseYear)
            purchaseLiveData.postSuccess(listItems)
        }
    }
}