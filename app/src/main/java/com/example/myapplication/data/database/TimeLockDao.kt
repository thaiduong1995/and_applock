package com.example.myapplication.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.model.TimeItem
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TimeLockDao {

    @Query("SELECT * FROM time_lock")
    abstract fun getTimeLock(): List<TimeItem>

    @Query("SELECT * FROM time_lock")
    abstract fun getTimeLockAsyn(): LiveData<List<TimeItem>>

    @Query("SELECT * FROM time_lock WHERE id = :id")
    abstract fun getTimeItemById(id: Long): TimeItem

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTimeLock(item: TimeItem)

    @Update
    abstract fun updateTimeLock(item: TimeItem)

    @Query("DELETE FROM time_lock WHERE id = :id")
    abstract fun deleteTimeLock(id: Long)

    @Query("DELETE FROM time_lock")
    abstract fun deleteAll()

    @Query("SELECT * FROM time_lock")
    abstract fun getTimeLockFlow(): Flow<List<TimeItem>>
}