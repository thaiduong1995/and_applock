package com.example.myapplication.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.database.TimeLockDao
import com.example.myapplication.data.model.DayItem
import com.example.myapplication.data.model.TimeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeLockViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private var timeLockDao: TimeLockDao
    var timeLocks: LiveData<List<TimeItem>>? = null
    private val listDays = MutableLiveData<ArrayList<DayItem>?>()
    val timeStartDefault = "0:00 AM"
    val timeEndDefault = "1:00 AM"

    fun saveTimeItem(item: TimeItem) {
        viewModelScope.launch(Dispatchers.IO) {
            timeLockDao.insertTimeLock(item)
        }
    }

    fun updateTimeItem(item: TimeItem) {
        viewModelScope.launch(Dispatchers.IO) {
            timeLockDao.updateTimeLock(item)
        }
    }

    fun getAllListTimeLock() {
        viewModelScope.launch(Dispatchers.IO) {
            timeLocks = timeLockDao.getTimeLockAsyn()
        }
    }

    fun deleteTimeItem(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            timeLockDao.deleteTimeLock(id)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            timeLockDao.deleteAll()
        }
    }

    init {
        listDays.value = arrayListOf(
            DayItem(name = "Mon"),
            DayItem(name = "Tue"),
            DayItem(name = "Wed"),
            DayItem(name = "Thu"),
            DayItem(name = "Fri"),
            DayItem(name = "Sat"),
            DayItem(name = "Sun"),
        )
        timeLockDao = AppDatabase.getInstance(context).getTimeLockDao()
        getAllListTimeLock()
    }
}