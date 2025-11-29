package com.example.myapplication.view_model

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.model.HistoryBrowser
import com.example.myapplication.data.model.TabBrowser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class BrowserViewModel @Inject constructor(
    @ApplicationContext val context: Context
) : BaseViewModel() {

    private val multipleTabsDao = AppDatabase.getInstance(context).multipleTabsDao()
    var listTabsBrowser: LiveData<List<TabBrowser>> = MutableLiveData()
    var listHistoryBrowser: LiveData<List<HistoryBrowser>> = MutableLiveData()
    var listBookMark: LiveData<List<HistoryBrowser>> = MutableLiveData()
    var listHistoryCurrentTab: MutableLiveData<List<HistoryBrowser>> = MutableLiveData()
    var listHistorySearch: MutableLiveData<List<HistoryBrowser>> = MutableLiveData()

    fun getDataListTabBrowser() {
        viewModelScope.launch(Dispatchers.IO) {
            listTabsBrowser = multipleTabsDao.getListTabBrowser()
        }
    }

    fun insertTab(tabBrowser: TabBrowser) {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.insertNewTabBrowser(tabBrowser)
        }
    }

    fun updateTab(tabBrowser: TabBrowser) {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.updateTabBrowser(tabBrowser)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.deleteAll()
        }
    }

    fun deleteTab(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.deleteItemTabBrowser(id)
        }
    }

    fun bitMapToString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val b = stream.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun insertHistory(history: HistoryBrowser) {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.insertHistory(history)
        }
    }

    fun deleteAllDataHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.deleteAllHistory()
        }
    }

    fun deleteAllDataBookMark() {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.deleteAllBookMark()
        }
    }

    fun deleteHistory(history: HistoryBrowser) {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.deleteHistory(history.id)
        }
    }

    fun deleteBookMark(history: HistoryBrowser) {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.updateHistoryByUrl(history.url, false)
        }
    }

    fun updateBookMark(history: HistoryBrowser) {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.updateHistoryByUrl(history.url, history.isBookMark)
        }
    }

    fun updateImageHistory(image: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            multipleTabsDao.updateImageHistory(image, id)
        }
    }

    fun getListHistoryCurrentTab(idTabBrowser: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            listHistoryCurrentTab.postValue(multipleTabsDao.getListHistoryByTab(idTabBrowser))
        }
    }

    fun searchHistory(keyWordSearch: String, listHistory: ArrayList<HistoryBrowser>) {
        if (keyWordSearch.isEmpty()) {
            listHistorySearch.postValue(listHistory)
        } else {
            listHistorySearch.postValue(listHistory.filter {
                it.url.lowercase().contains(keyWordSearch) || it.title.lowercase()
                    .contains(keyWordSearch)
            })
        }
    }

    init {
        getDataListTabBrowser()
        listHistoryBrowser = multipleTabsDao.getListHistory()
        listBookMark = multipleTabsDao.getListBookMark()
    }
}