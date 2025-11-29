package com.example.myapplication.data.database

import androidx.room.*
import com.example.myapplication.data.model.RecentSearch

@Dao
abstract class SearchLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSearchLocation(searchLocation: RecentSearch)

    @Query("SELECT * FROM recentsearch ORDER BY id DESC LIMIT 20")
    abstract fun getLocationsSearchHistory(): MutableList<RecentSearch>
}