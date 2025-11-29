package com.example.myapplication.service

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

/**
 * Created by Thinhvh on 26/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */

object AppForegroundObserve {

    fun getCurrentAppRunningFlow(context: Context, condition: Boolean, period: Long) = flow {
        while (condition) {
            val mUsageStatsManager =
                context.getSystemService(Service.USAGE_STATS_SERVICE) as UsageStatsManager?
            val time = System.currentTimeMillis()

            val usageEvents = mUsageStatsManager?.queryEvents(
                time - 1000 * 20,
                time
            )
            val event = UsageEvents.Event()
            while (usageEvents?.hasNextEvent() == true) {
                usageEvents.getNextEvent(event)
            }

            if (event.className == "com.android.packageinstaller.UninstallerActivity") {
                Log.d("hungnv", "getCurrentAppRunningFlow: ${Gson().toJson(event.className)}")
                emit(event.className)
            } else if (!TextUtils.isEmpty(event.packageName) && event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                val pkgName = if (isRecentActivity(event.className)) {
                    event.className
                } else event.packageName
                emit(pkgName)
            }

//            val stats = mUsageStatsManager?.queryUsageStats(
//                UsageStatsManager.INTERVAL_DAILY,
//                time - 1000 * 20,
//                time
//            )
//            if (stats != null) {
//                val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
//                for (usageStats in stats) {
//                    mySortedMap[usageStats.lastTimeUsed] = usageStats
//                }
//                if (!mySortedMap.isEmpty()) {
//                    mySortedMap[mySortedMap.lastKey()]?.let {
//                        emit(UsageStatsWrapper(it))
//                    }
//                }
//            }
            delay(period)
        }
    }
}

fun isRecentActivity(className: String?): Boolean {
    val recentActivity = "RECENT_ACTIVITY"
    return recentActivity.equals(className, true)
}

//class UsageStatsWrapper(val usageStats: UsageStats)