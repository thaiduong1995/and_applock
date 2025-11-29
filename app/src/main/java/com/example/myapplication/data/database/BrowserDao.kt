package com.example.myapplication.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.model.HistoryBrowser
import com.example.myapplication.data.model.TabBrowser

@Dao
interface BrowserDao {

    @Insert
    fun insertNewTabBrowser(tabBrowserEntity: TabBrowser)

    @Update
    fun updateTabBrowser(tabBrowserEntity: TabBrowser)

    @Query("DELETE FROM MultipleTabsBrowser WHERE id = :id")
    fun deleteItemTabBrowser(id: Long)

    @Query("SELECT * FROM MultipleTabsBrowser ORDER BY id ASC")
    fun getListTabBrowser(): LiveData<List<TabBrowser>>

    @Query("DELETE FROM MultipleTabsBrowser")
    fun deleteAll()

    @Insert
    fun insertHistory(historyBrowserEntity: HistoryBrowser)

    @Query("UPDATE HistoryBrowser SET isDeleteHistory = 1 WHERE id = :id")
    fun deleteHistory(id: Long)

    @Query("UPDATE HistoryBrowser SET isBookMark = :isBookMark WHERE url = :url")
    fun updateHistoryByUrl(url: String, isBookMark: Boolean)

    @Query("UPDATE HistoryBrowser SET isDeleteHistory = 1")
    fun deleteAllHistory()

    @Query("UPDATE HistoryBrowser SET isBookMark = 0")
    fun deleteAllBookMark()

    @Query("SELECT * FROM HistoryBrowser WHERE isDeleteHistory = 0 ORDER BY id ASC")
    fun getListHistory(): LiveData<List<HistoryBrowser>>

    @Query("SELECT * FROM HistoryBrowser WHERE isBookMark = 1 ORDER BY id ASC")
    fun getListBookMark(): LiveData<List<HistoryBrowser>>

    @Query("UPDATE HistoryBrowser SET image = :image WHERE id = :id")
    fun updateImageHistory(image: String, id: Long)

    @Query("SELECT * FROM HistoryBrowser WHERE idTabBrowser = :idTabBrowser ORDER BY id ASC")
    fun getListHistoryByTab(idTabBrowser: Long): List<HistoryBrowser>
}