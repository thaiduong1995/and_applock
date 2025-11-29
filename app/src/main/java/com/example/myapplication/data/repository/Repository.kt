package com.example.myapplication.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.model.AppData
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thinhvh on 22/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Singleton
class Repository @Inject constructor() {

    fun fetchInstallApp(context: Context): ArrayList<AppData> {
        val resolveInfoList: List<ResolveInfo> = context.packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
            PackageManager.MATCH_ALL or PackageManager.MATCH_DISABLED_COMPONENTS
        )

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -7)

        val appDataList: ArrayList<AppData> = arrayListOf()
        resolveInfoList.forEach { resolveInfo ->
            with(resolveInfo) {
                if (activityInfo.packageName != context.packageName &&
                    appDataList.find { it.packageName.lowercase() == activityInfo.packageName.lowercase() } == null
                ) {
                    val pkgInfo = context.packageManager.getPackageInfo(activityInfo.packageName, 0)
                    val installTime = pkgInfo.firstInstallTime

                    val appData = AppData(
                        packageName = activityInfo.packageName,
                        appName = getLabelByPackage(context, activityInfo.packageName)
                    )
                    appData.systemApp = isSystemPackage(resolveInfo)
                    if (installTime > calendar.timeInMillis) {
                        appData.isNewInstall = true
                    }
                    appData.timeInstall = installTime

                    Log.d("thinhvhxxx", "fetchInstallApp: ${appData.packageName}")
                    appDataList.add(appData)
                }
            }
        }
        return appDataList
    }

    fun getLabelByPackage(context: Context, packageName: String): String {
        return context.packageManager.getApplicationLabel(
            context.packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )
        ).toString()
    }

    fun fetchLockedApp(context: Context): List<AppData> {
        return AppDatabase.getInstance(context).getLockedAppsDao().getLockedAppsSync()
    }

    private fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
        return resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }
}