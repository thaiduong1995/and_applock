package com.example.myapplication.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.model.CommonSelector
import com.example.myapplication.data.model.Intruder
import com.example.myapplication.data.model.UnlockCount
import com.example.myapplication.data.model.UnlockCountType
import com.example.myapplication.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class CaptureIntrudersViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val preference: PreferenceHelper
) : BaseViewModel() {

    var listUnlockCountLiveData = MediatorLiveData<List<CommonSelector>>()
    var listImageLiveData: LiveData<List<Intruder>>? = null

    private fun getListUnlockCount(context: Context) {
        viewModelScope.launch {
            val unlockCount = preference.getUnlockCount()
            val listCommonSelector = arrayListOf<CommonSelector>()
            listCommonSelector.addAll(
                UnlockCountType.entries.map {
                    UnlockCount(
                        name = String.format(context.getString(R.string.after_time, it.id)),
                        resId = R.string.after_time,
                        value = it.id,
                        isSelected = false
                    )
                }
            )
            listCommonSelector.find { it.value == unlockCount }?.isSelected = true
            listUnlockCountLiveData.postValue(listCommonSelector)
        }
    }

    fun getUnlockCount(): Int {
        return preference.getUnlockCount()
    }

    fun saveUnlockCount(commonSelector: CommonSelector) {
        preference.setUnlockCount(commonSelector.value)
    }

    fun isCaptureIntrudersEnabled(): Boolean {
        return preference.isCaptureIntrudersEnabled()
    }

    fun setEnableCaptureIntruders(enable: Boolean) {
        preference.setEnableCaptureIntruders(enable)
    }

    fun getIntruder() {
        viewModelScope.launch(Dispatchers.IO) {
            val listIntruder =
                AppDatabase.getInstance(context).getIntruderDao().getIntruders()
            listIntruder.value?.forEach {
                try {
                    val file = File(it.imageUrl)
                    if (!file.exists()) {
                        AppDatabase.getInstance(context).getIntruderDao()
                            .deleteIntruder(it.id)
                    }
                } catch (e: Exception) {
                    Log.d("THINHVH", "loadImage: ERROR ${e.message}")
                }
            }
            listImageLiveData = listIntruder
        }
    }

    fun deleteIntruder(intruder: Intruder) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance(context).getIntruderDao().deleteIntruder(intruder.id)
        }
    }

    init {
        getListUnlockCount(context)
    }
}