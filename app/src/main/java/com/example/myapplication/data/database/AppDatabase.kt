package com.example.myapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.model.AppData
import com.example.myapplication.data.model.CustomTheme
import com.example.myapplication.data.model.GroupWifi
import com.example.myapplication.data.model.HistoryBrowser
import com.example.myapplication.data.model.Intruder
import com.example.myapplication.data.model.ItemWifi
import com.example.myapplication.data.model.LocationLock
import com.example.myapplication.data.model.RecentSearch
import com.example.myapplication.data.model.TabBrowser
import com.example.myapplication.data.model.TimeItem

/**
 * Created by Thinhvh on 22/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Database(
    entities = [
        AppData::class,
        Intruder::class,
        LocationLock::class,
        TabBrowser::class,
        HistoryBrowser::class,
        ItemWifi::class,
        GroupWifi::class,
        TimeItem::class,
        RecentSearch::class,
        CustomTheme::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLockedAppsDao(): LockAppsDao
    abstract fun multipleTabsDao(): BrowserDao
    abstract fun getIntruderDao(): IntruderDao
    abstract fun getLocationDao(): LocationDao
    abstract fun getWfiDao(): WifiDao
    abstract fun getGroupWifiDao(): GroupWifiDao
    abstract fun getSearchLocationDao(): SearchLocationDao
    abstract fun getTimeLockDao(): TimeLockDao
    abstract fun getCustomThemeDao(): CustomThemeDao

    companion object {
        private val DATABASE_NAME = "APPLOCK"
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}