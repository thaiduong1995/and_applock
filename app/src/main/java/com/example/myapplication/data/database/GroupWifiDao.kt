package com.example.myapplication.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.model.GroupWifi
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupWifiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupWifi(groupWifi: GroupWifi)

    @Query("SELECT * FROM groupwifi ORDER by id DESC")
    fun getAllGroupWifisAsyn(): LiveData<List<GroupWifi>>

    @Query("SELECT * FROM groupwifi ORDER by id DESC")
    fun getAllGroupWifisFlow(): Flow<List<GroupWifi>>

    @Query("SELECT * FROM groupwifi")
    fun getAllGroupWifiSync(): List<GroupWifi>

    @Query("DELETE FROM groupwifi WHERE id = :id")
    fun deleteGroupWifi(id: Long)
}
