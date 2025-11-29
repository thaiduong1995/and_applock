package com.example.myapplication.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.model.AppData
import com.example.myapplication.data.model.CustomTheme
import com.example.myapplication.data.model.Intruder
import com.example.myapplication.data.model.liveData.MutableStateLiveData
import com.example.myapplication.utils.PreferenceHelper
import com.example.myapplication.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockOverlayViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val preference: PreferenceHelper
) : ViewModel() {

    var appLiveData = MutableStateLiveData<AppData>()
    var listCustomTheme: List<CustomTheme> = arrayListOf()

    fun getAppData(packageName: String) {
        appLiveData.postLoading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentAppData = Utils.getAppInfo(context, packageName)
                currentAppData?.let {
                    appLiveData.postSuccess(currentAppData)
                }
            } catch (e: Exception) {
                appLiveData.postError(e.message)
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            listCustomTheme =
                AppDatabase.getInstance(context).getCustomThemeDao().getAllCustomThemeSync()
        }
    }

    fun getCurrentCustomTheme(): CustomTheme? {
        val currentCustomThemeId = preference.getCustomThemeId()
        return listCustomTheme.find { it.id == currentCustomThemeId }
    }

    fun addIntruderToDB(intruder: Intruder) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance(context).getIntruderDao()
                .addIntruder(intruder)
        }
    }
}