package com.example.myapplication.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.model.AppData

/**
 * Created by Thinhvh on 29/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Dao
abstract class LockAppsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun lockApp(lockedAppEntity: AppData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun lockApps(lockedAppEntityList: List<AppData>)

    @Query("SELECT * FROM locked_app")
    abstract fun getLockedApps(): LiveData<List<AppData>>

    @Query("SELECT * FROM locked_app")
    abstract fun getLockedAppsSync(): List<AppData>

    @Query("DELETE FROM locked_app WHERE packageName = :packageName")
    abstract fun unlockApp(packageName: String)

    @Query("DELETE FROM locked_app")
    abstract fun unlockAll()

    @Query("SELECT * FROM locked_app WHERE packageName = :pkgName")
    abstract fun getAppInfo(pkgName: String): AppData
}