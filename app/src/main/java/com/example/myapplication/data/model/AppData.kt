package com.example.myapplication.data.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.errorprone.annotations.Keep

/**
 * Created by Thinhvh on 24/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Keep
@Entity(tableName = "locked_app")
data class AppData(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "packageName")
    val packageName: String,
    @ColumnInfo(name = "appName")
    val appName: String,
    @ColumnInfo(name = "isLock")
    var isLock: Boolean = false,
) {
    @Ignore
    var isReComment: Boolean = false

    @Ignore
    var isCanLock: Boolean = true

    @Ignore
    var nextTimeLock: Long = 0

    @Ignore
    var systemApp: Boolean = false

    @Ignore
    var isNewInstall: Boolean = false

    @Ignore
    var timeInstall: Long = 0L

    @Ignore
    var selected: Boolean = false

    @Ignore
    var fakeIcon: Bitmap? = null
}