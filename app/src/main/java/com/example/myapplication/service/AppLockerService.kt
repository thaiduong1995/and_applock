package com.example.myapplication.service

import android.Manifest
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import com.example.myapplication.data.model.*
import com.example.myapplication.notification.ServiceNotificationManager
import com.example.myapplication.service.AppForegroundObserve.getCurrentAppRunningFlow
import com.example.myapplication.ui.activities.LockActivity
import com.example.myapplication.ui.activities.LockFakeCoverActivity
import com.example.myapplication.utils.*
import com.example.myapplication.utils.Constants.KEY_BUNDLE
import com.example.myapplication.utils.Constants.KEY_PACKAGE_NAME
import com.example.myapplication.utils.Constants.KEY_UNLOCK_SUCCESS
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@AndroidEntryPoint
class AppLockerService : LifecycleService() {

    private val TAG = "AppLockerService"
    lateinit var serviceNotificationManager: ServiceNotificationManager
    private var isOverlayShowing = false
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var isServiceRunning = false
    private var isScreenOff = false
    private var lastForegroundAppPackage: String = ""
    private var listLockedApp = listOf<AppData>()
    private var _lockViewModel: LockServiceViewModel? = null
    private val lockViewModel: LockServiceViewModel
        get() = _lockViewModel!!

    //    private var listLocationLock = listOf<LocationLock>()
//    private var listTimeLock = listOf<TimeItem>()
//    private var listGroupWifiLock = listOf<GroupWifi>()
//    private var listWifiLock = listOf<ItemWifi>()
    private var currentLocation: LatLng? = null
    private val devicePolicyManager by lazy {
        getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
    private val adminComponentName by lazy {
        ComponentName(this, AppLockDeviceAdminReceiver::class.java)
    }

    @Inject
    lateinit var preferences: PreferenceHelper

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true

        initViewModel()
        initObserve()
        initializeAppLockerNotification()
        observeForegroundApplication()
        registerScreenReceiver()
        registerAppInstallReceiver()
        startLockChecker()
//        startLocationTracker()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let {
            if (intent.hasExtra(KEY_BUNDLE)) {
                intent.getBundleExtra(KEY_BUNDLE)?.let {
                    val isUnLock = it.getBoolean(KEY_UNLOCK_SUCCESS)
                    val pkgName = it.getString(KEY_PACKAGE_NAME)

                    isUnLock.let {
                        isOverlayShowing = false
                    }

                    if (isUnLock) {
                        pkgName?.let {
                            setNextTimeLockApp(pkgName)
                        }
                    }

                    val onClick = it.getString(Constants.KEY_LONG_CLICK_SUCCESS)
                    onClick?.let { pkg ->
                        onLongClick(pkg)
                        setNextTimeLockApp(pkg)
                    }
                }
            }
        }
        return START_STICKY
    }

    private fun setNextTimeLockApp(pkgName: String) {
        listLockedApp.find { it.packageName == pkgName }?.let { appData ->
            if (lastForegroundAppPackage == appData.packageName) {
                getLockFrequencyType()?.let { type ->
                    if (type.id != LockFrequencyType.ALWAYS.id && type.id != LockFrequencyType.AFTER_SCREEN_LOCK.id) {
                        appData.nextTimeLock = System.currentTimeMillis() + type.time
                    }
                }
            }
        }
    }

    private fun getLockFrequencyType(): LockFrequencyType? {
        val lockFrequencyTypeId = preferences.getLockFrequencyType()
        return LockFrequencyType.entries.find { it.id == lockFrequencyTypeId }
    }

    private fun initViewModel() {
        _lockViewModel = LockServiceViewModel(this)
    }

    private fun initObserve() {
        lockViewModel.listLockedAppLiveData?.observe(this@AppLockerService) {
            it?.let { listData ->
                listLockedApp = listData
            }
        }

//        lockViewModel.locationLocksLiveData?.observe(this@AppLockerService) {
//            it?.let { listData ->
//                Log.d("thinhvh", "locationLocksLiveData: UPDATED ")
//                listLocationLock = listData
//                if (currentLocation == null) {
//                    startLocationTracker()
//                }
//            }
//        }
//
//        lockViewModel.timeLocksLiveData?.observe(this@AppLockerService) {
//            it?.let { listData ->
//                Log.d("thinhvh", "listLockedAppLiveData: UPDATED ")
//                listTimeLock = listData
//            }
//        }
//
//        lockViewModel.groupWifiLiveData?.observe(this@AppLockerService) {
//            it?.let { listData ->
//                Log.d("thinhvh", "groupWifiLiveData: UPDATED ")
//                listGroupWifiLock = listData
//            }
//        }
//
//
//        lockViewModel.wifiLiveData?.observe(this@AppLockerService) {
//            it?.let { listData ->
//                Log.d("thinhvh", "wifiLiveData: UPDATED ")
//                listWifiLock = listData
//            }
//        }
    }

    private fun registerScreenReceiver() {
        val screenFilter = IntentFilter()
        screenFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenOnOffReceiver, screenFilter)
    }

    private fun registerAppInstallReceiver() {
        val screenFilter = IntentFilter()
        screenFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        screenFilter.addAction(Intent.ACTION_PACKAGE_INSTALL)
        screenFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        screenFilter.addDataScheme("package")
        registerReceiver(appInstallReceiver, screenFilter)
    }

    private var appInstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_PACKAGE_ADDED || intent?.action == Intent.ACTION_PACKAGE_INSTALL) {
                onInstallNewApp(intent.data.toString())
            } else if (intent?.action == Intent.ACTION_PACKAGE_REMOVED) {
                removeAppFromDb(intent.data.toString())
            }
        }
    }

    private fun onInstallNewApp(pkgName: String?) {
        if (preferences.isLockNewAppEnable()) {
            pkgName?.let {
                if (pkgName.contains("package:")) {
                    val newPkgName = pkgName.removePrefix("package:")
                    val appData = Utils.getAppInfo(this, newPkgName)
                    appData?.let {
                        lockViewModel.lockApp(appData)
                    }
                } else {
                    val appData = Utils.getAppInfo(this, it)
                    appData?.let {
                        lockViewModel.lockApp(appData)
                    }
                }
            }
        }
    }

    private var screenOnOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> onScreenOn()
                Intent.ACTION_SCREEN_OFF -> onScreenOff()
            }
        }
    }

    private fun removeAppFromDb(pkgName: String) {
        var newPackage = pkgName
        pkgName.let {
            if (pkgName.contains("package:")) {
                newPackage = pkgName.removePrefix("package:")
            }
        }

        if (newPackage.isNotEmpty()) {
            lockViewModel.removeAppData(this, newPackage)
        }
    }

    private fun onScreenOn() {
        if (preferences.getLockFrequencyType() == LockFrequencyType.AFTER_SCREEN_LOCK.id || preferences.getLockFrequencyType() == LockFrequencyType.ALWAYS.id) {
            val currentApp = lastForegroundAppPackage
            lastForegroundAppPackage = ""
            listLockedApp.find { it.packageName == currentApp }?.let {
                it.isCanLock = true
            }
        }
        startLockChecker()
        observeForegroundApplication()
    }

    private fun onScreenOff() {
        stopForegroundApplicationObserver()
        isScreenOff = true
    }

    private fun stopForegroundApplicationObserver() {
        job.cancelChildren()
    }

    private fun observeForegroundApplication() {
        scope.launch(Dispatchers.IO) {
            if (!isActive) return@launch
            getCurrentAppRunningFlow(this@AppLockerService, isServiceRunning, QUERY_TIME).filter {
                it.isNotEmpty()
            }.collect { packageName ->
                packageName?.let {
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "onAppForeground $packageName")
                        if (preferences.isAppLockEnabled()) {
                            if (it == PACKAGE_UNINSTALL && devicePolicyManager.isAdminActive(
                                    adminComponentName
                                )
                            ) {
                                if (packageName != lastForegroundAppPackage) {
                                    hideOverlay()
                                    if (PermissionChecker.checkOverlayPermission(this@AppLockerService)) {
                                        showOverlay(packageName)
                                    }
                                }
                                lastForegroundAppPackage = packageName
                            } else {
                                if (getLockFrequencyType() == LockFrequencyType.AFTER_SCREEN_LOCK) {
                                    onAppScreenForeground(it)
                                } else {
                                    onAppForeground(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startLockChecker() {
        scope.launch(Dispatchers.IO) {
            if (!isActive) return@launch
            var checkerFlow: Flow<Unit> = flow {
                while (isActive) {
                    emit(Unit)
                    delay(3000)
                }
            }

            checkerFlow.collect {
                getLockFrequencyType()?.let { lockFrequencyType ->
                    if (lockFrequencyType != LockFrequencyType.AFTER_SCREEN_LOCK && lockFrequencyType != LockFrequencyType.ALWAYS) {
                        if (listLockedApp.isNotEmpty()) {
                            val listLock = StringBuilder()
                            val currentTime = System.currentTimeMillis()
                            listLockedApp.forEach { appData ->
                                appData.isCanLock = currentTime >= appData.nextTimeLock

                                if (appData.isCanLock) {
                                    listLock.append(appData.appName)
                                    listLock.append("-")
                                }

                                if (lastForegroundAppPackage == appData.packageName && appData.isCanLock && isOverlayShowing.not()) {
                                    var currentApp = lastForegroundAppPackage
                                    lastForegroundAppPackage = ""
                                }
                            }
                        }
                    } else if (lockFrequencyType == LockFrequencyType.ALWAYS) {
                        listLockedApp.forEach { appData ->
                            appData.isCanLock = true
                        }
                    }
                }
            }
        }
    }

//    private fun startLocationTracker() {
//        if (PermissionChecker.isHaveLocationPermission(this)) {
//            try {
//                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                    location?.let {
//                        currentLocation = LatLng(it.latitude, it.longitude)
//                    }
//                }
//                try {
//                    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//                    val locationListener = object : LocationListener {
//                        override fun onLocationChanged(location: Location) {
//                            val latitude = location.latitude
//                            val longitude = location.longitude
//                            currentLocation = LatLng(latitude, longitude)
//                            Log.d("thinhvh", "onLocationChanged: ${latitude} ${longitude}")
//                        }
//
//                        override fun onProviderEnabled(provider: String) {
//                            Log.d("thinhvh", "onProviderEnabled: ${provider}")
//                        }
//
//                        override fun onProviderDisabled(provider: String) {
//                            Log.d("thinhvh", "onProviderDisabled: ${provider}")
//                        }
//
//                        override fun onStatusChanged(
//                            provider: String?, status: Int, extras: Bundle?
//                        ) {
//                            Log.d("thinhvh", "onStatusChanged: ${status}")
//                        }
//
//                    }
//                    if (ActivityCompat.checkSelfPermission(
//                            this, Manifest.permission.ACCESS_FINE_LOCATION
//                        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                            this, Manifest.permission.ACCESS_COARSE_LOCATION
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {
//                        locationManager.requestLocationUpdates(
//                            LocationManager.GPS_PROVIDER, 0L, 0f, locationListener
//                        )
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    private fun onAppForeground(packageName: String) {
        isScreenOff = false
        if (this.packageName != packageName) {
            if (packageName != lastForegroundAppPackage) {
                hideOverlay()
                if (lockViewModel.isAppLocked(this, listLockedApp, packageName)) {
                    val isCanLock = lockViewModel.isCanLockApp(listLockedApp, packageName)
                    if (isCanLock) {
//                        if (onLockCondition() || isInsideLockTime() || isWifiLock() || isInsideLockLocation()) {
//                            if (PermissionChecker.checkOverlayPermission(this)) {
//                                Handler(Looper.getMainLooper()).postDelayed(
//                                    {
//                                        showOverlay(packageName)
//                                    },
//                                    if (packageName == "com.facebook.katana" || packageName == "com.zing.zalo") 500 else 0
//                                )
//                            }
//                        } else {
                        if (PermissionChecker.checkOverlayPermission(this)) {
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    if (preferences.isFakeCoverEnable()) {
                                        startLockCoverActivity(packageName)
                                    } else {
                                        showOverlay(packageName)
                                    }
                                },
                                if (packageName == "com.facebook.katana" || packageName == "com.zing.zalo") 500 else 0
                            )
                        }
//                        }
                    }
                }
            }
            lastForegroundAppPackage = packageName
        }
    }

    private fun onAppScreenForeground(packageName: String) {
        if (!isScreenOff) {
            return
        }
        if (getLockFrequencyType() != LockFrequencyType.AFTER_SCREEN_LOCK) {
            return
        }
        isScreenOff = false
        if (this.packageName != packageName) {
            if (packageName != lastForegroundAppPackage) {
                hideOverlay()
                if (lockViewModel.isAppLocked(this, listLockedApp, packageName)) {
                    val isCanLock = lockViewModel.isCanLockApp(listLockedApp, packageName)
                    if (isCanLock) {
//                        if (onLockCondition() || isInsideLockTime() || isWifiLock() || isInsideLockLocation()) {
//                            if (PermissionChecker.checkOverlayPermission(this)) {
//                                Handler(Looper.getMainLooper()).postDelayed(
//                                    {
//                                        showOverlay(packageName)
//                                    },
//                                    if (packageName == "com.facebook.katana" || packageName == "com.zing.zalo") 500 else 0
//                                )
//                            }
//                        } else {
                        if (PermissionChecker.checkOverlayPermission(this)) {
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    if (preferences.isFakeCoverEnable()) {
                                        startLockCoverActivity(packageName)
                                    } else {
                                        showOverlay(packageName)
                                    }
                                },
                                if (packageName == "com.facebook.katana" || packageName == "com.zing.zalo") 500 else 0
                            )
//                            }
                        }
                    }
                }
            }
            lastForegroundAppPackage = packageName
        }
    }

    private fun onLongClick(packageName: String) {
        hideOverlay()
//        if (onLockCondition() || isInsideLockTime() || isWifiLock() || isInsideLockLocation()) {
        if (PermissionChecker.checkOverlayPermission(this)) {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    showOverlayLock(packageName)
                },
                if (packageName == "com.facebook.katana" || packageName == "com.zing.zalo") 500 else 0
            )
        }
//        }
    }

//    private fun onLockCondition(): Boolean {
//        val wifiLockEnable = listWifiLock.filter { it.enabled }.isNotEmpty()
//        val timeLockEnable = listTimeLock.filter { it.enable }.isNotEmpty()
//        val locationLockEnable = listLocationLock.filter { it.enabled }.isNotEmpty()
//        if (!wifiLockEnable && !timeLockEnable && !locationLockEnable) {
//            return true
//        }
//        return false
//    }

    private fun initializeAppLockerNotification() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            serviceNotificationManager = ServiceNotificationManager(this)
            val notification = serviceNotificationManager.createNotification()
            NotificationManagerCompat.from(applicationContext)
                .notify(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
            startForeground(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
            return
        }

    }

    private fun showOverlay(lockedAppPackageName: String) {
        Log.d(TAG, "showOverlay: ")
        if (isOverlayShowing.not()) {
            isOverlayShowing = true
            if (preferences.isFakeCoverEnable()) {
                startLockCoverActivity(lockedAppPackageName)
            } else {
                startLockActivity(lockedAppPackageName)
            }
        }
    }

    private fun showOverlayLock(lockedAppPackageName: String) {
        Log.d(TAG, "showOverlayLock: ")
        if (isOverlayShowing.not()) {
            isOverlayShowing = true
            startLockActivity(lockedAppPackageName)
        }
    }

    private fun startLockActivity(lockedAppPackageName: String) {
        val intent = LockActivity.newIntent(applicationContext, lockedAppPackageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun startLockCoverActivity(lockedAppPackageName: String) {
        val intent = LockFakeCoverActivity.newIntent(applicationContext, lockedAppPackageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun hideOverlay() {
        if (isOverlayShowing) {
            isOverlayShowing = false
        }
    }

//    private fun isInsideLockLocation(): Boolean {
//        if (currentLocation == null) {
//            startLocationTracker()
//        }
//        currentLocation?.let { currentLocation ->
//            val currentLoc = Location("").apply {
//                latitude = currentLocation.latitude
//                longitude = currentLocation.longitude
//            }
//            listLocationLock.filter { it.enabled }.forEach { savedLocation ->
//                val lockLocation = Location("")
//                lockLocation.apply {
//                    latitude = savedLocation.latitude
//                    longitude = savedLocation.longitude
//                }
//
//                val distance = currentLoc.distanceTo(lockLocation)
//                if (savedLocation.isInverse && distance > savedLocation.radius) return true
//                if (!savedLocation.isInverse && distance < savedLocation.radius) return true
//            }
//        }
//        return false
//    }

//    private fun isInsideLockTime(): Boolean {
//        val timeItems: List<TimeItem> = listTimeLock.filter { it.enable }
//        for (timeItem in timeItems) {
//            if (Utils.isCurrentTimeInsideTimeRange(timeItem)) {
//                return true
//            }
//        }
//        return false
//    }
//
//    private fun isWifiLock(): Boolean {
//        if (PermissionChecker.hasWifiPermission(this)) {
//            val mac = this.currentWifi
//            mac?.let { currentMac ->
//                val listGroupEnable = listGroupWifiLock.filter { it.enabled }
//                listGroupEnable.forEach { groupWifi ->
//                    listWifiLock.filter { it.enabled && it.groupId == groupWifi.id }.forEach {
//                        if (it.enabled && it.bssId == currentMac.bssId) {
//                            return true
//                        }
//                    }
//                }
//            }
//
//        }
//        return false
//    }

    override fun onDestroy() {
        isServiceRunning = false
        unregisterReceiver(screenOnOffReceiver)
        unregisterReceiver(appInstallReceiver)
        job.cancelChildren()
        stopSelf()
        ServiceStarter.restartService(this)
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID_APPLOCKER_SERVICE = 1
        private const val QUERY_TIME = 100L
        private const val PACKAGE_UNINSTALL = "com.android.packageinstaller.UninstallerActivity"
    }
}