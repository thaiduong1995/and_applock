package com.example.myapplication.view_model

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.model.AppData
import com.example.myapplication.data.model.liveData.MutableStateLiveData
import com.example.myapplication.data.repository.Repository
import com.example.myapplication.utils.NetworkHelper
import com.example.myapplication.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FakeIconViewModel @Inject constructor(
    private val mainRepository: Repository,
    private val networkHelper: NetworkHelper,
    private val preference: PreferenceHelper
) : BaseViewModel() {

    var listAppLiveData = MutableStateLiveData<List<AppData>>()
    private var listApp = mutableListOf<AppData>()

    fun getAllApp(context: Context) {
        listAppLiveData.postLoading()

        viewModelScope.launch {
            try {
                listApp = mainRepository.fetchInstallApp(context)
                listAppLiveData.postSuccess(listApp)
            } catch (e: Exception) {
                listAppLiveData.postError(e.message)
            }
        }
    }

    fun searchApp(keyWordSearch: String) {
        listAppLiveData.postLoading()
        if (keyWordSearch.isEmpty()) {
            listAppLiveData.postSuccess(listApp)
        } else {
            listAppLiveData.postSuccess(listApp.filter {
                it.appName.lowercase().contains(keyWordSearch.lowercase())
            })
        }
    }
}