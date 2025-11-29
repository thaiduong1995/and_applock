package com.example.myapplication.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.model.LocationLock
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLocation(locationLock: LocationLock)

    @Update
    abstract fun updateLocation(locationLock: LocationLock)

    @Query("SELECT * FROM location ORDER by id DESC")
    abstract fun getAllLocations(): LiveData<List<LocationLock>>

    @Query("SELECT * FROM location ORDER by id DESC")
    abstract fun getLocationsSync(): List<LocationLock>

    @Query("SELECT * FROM location ORDER by id DESC")
    abstract fun getLocationsFlow(): Flow<List<LocationLock>>

    @Query("DELETE FROM location WHERE id = :id")
    abstract fun deleteLocation(id: Long)

    @Query("SELECT EXISTS(SELECT * FROM location WHERE locationName = :name)")
    abstract fun isExits(name: String): Boolean
}