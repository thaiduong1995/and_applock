package com.example.myapplication.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.model.ItemWifi

@Dao
interface WifiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWifi(itemWifi: ItemWifi)

    @Query("SELECT * FROM wifi")
    fun getAllWifis(): List<ItemWifi>

    @Query("SELECT * FROM wifi")
    fun getAllWifisAsync(): LiveData<List<ItemWifi>>

    @Query("DELETE FROM wifi WHERE bssid = :bssid")
    fun deleteWifi(bssid: String)
}
