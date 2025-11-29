package com.example.myapplication.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.model.Intruder

/**
 * Created by Thinhvh on 07/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Dao
abstract class IntruderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addIntruder(intruder: Intruder)

    @Query("SELECT * FROM intruder")
    abstract fun getIntruders(): LiveData<List<Intruder>>

    @Query("SELECT * FROM intruder")
    abstract fun getIntrudersSync(): List<Intruder>

    @Query("DELETE FROM intruder WHERE id = :id")
    abstract fun deleteIntruder(id: Int)

}