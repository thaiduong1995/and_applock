package com.example.myapplication.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.model.CustomTheme

@Dao
interface CustomThemeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(customTheme: CustomTheme)

    @Query("SELECT * FROM custom_themes ORDER BY id DESC")
    fun getAllCustomTheme(): LiveData<List<CustomTheme>>

    @Query("SELECT * FROM custom_themes ORDER BY id DESC")
    fun getAllCustomThemeSync(): List<CustomTheme>

    @Query("DELETE FROM custom_themes WHERE id = :id")
    fun delete(id: Int)
}
