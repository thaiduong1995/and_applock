package com.example.myapplication.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.database.WifiDao
import com.example.myapplication.data.model.GroupWifi
import com.example.myapplication.data.model.ItemWifi
import com.example.myapplication.data.model.liveData.SingleEvent
import com.example.myapplication.utils.scanWifi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseWifiFragmentViewModel @Inject constructor(
    @ApplicationContext val context: Context
) : ViewModel() {

    private val wifiDao: WifiDao = AppDatabase.getInstance(context).getWfiDao()
    private val groupWifiDao = AppDatabase.getInstance(context).getGroupWifiDao()
    val wifisLiveData: MutableLiveData<List<ItemWifi>> = MutableLiveData()
    val loadingAllWifiLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val insertAllWifiLiveData: MutableLiveData<SingleEvent<Boolean>> = MutableLiveData()
    private val originWifis: MutableList<ItemWifi> = mutableListOf()

    fun scanWifi(groupWifi: GroupWifi?) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingAllWifiLiveData.postValue(true)
            groupWifi?.let { groupWifi ->
                wifiDao.getAllWifis().filter {
                    it.groupId == groupWifi.id
                }.let {
                    setData(it)
                }
            } ?: let {
                context.scanWifi { wifis ->
                    setData(wifis)
                }
            }
        }
    }

    private fun setData(wifis: List<ItemWifi>) {
        val sortedWifis = wifis.sortedByDescending { it.enabled }
        wifisLiveData.postValue(sortedWifis)
        loadingAllWifiLiveData.postValue(false)
        originWifis.clear()
        originWifis.addAll(sortedWifis)
    }

    fun filterWifi(input: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingAllWifiLiveData.postValue(true)
            wifisLiveData.postValue(originWifis.filter {
                it.ssid.contains(input, ignoreCase = true)
            })
            loadingAllWifiLiveData.postValue(false)
        }
    }

    fun saveSelectedWifis() {
        viewModelScope.launch(Dispatchers.IO) {
            loadingAllWifiLiveData.postValue(true)
            originWifis.forEach {
                wifiDao.insertWifi(it)
            }
            val childWifiCount = originWifis.count { it.enabled }
            originWifis.firstOrNull()?.let {
                groupWifiDao.insertGroupWifi(GroupWifi(it.groupId, childWifiCount))
            }

            loadingAllWifiLiveData.postValue(false)
            insertAllWifiLiveData.postValue(SingleEvent(true))
        }
    }
}
