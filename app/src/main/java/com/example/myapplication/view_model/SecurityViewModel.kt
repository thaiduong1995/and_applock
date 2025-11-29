package com.example.myapplication.view_model

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.model.CommonSelector
import com.example.myapplication.data.model.Frequency
import com.example.myapplication.data.model.LockAnimation
import com.example.myapplication.data.model.LockAnimationType
import com.example.myapplication.data.model.LockFrequencyType
import com.example.myapplication.extention.getNameAnimation
import com.example.myapplication.extention.getNameLockFrequency
import com.example.myapplication.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val preference: PreferenceHelper
) : BaseViewModel() {

    var listLockFrequencyLiveData = MediatorLiveData<List<CommonSelector>>()
    var listAnimationLiveData = MediatorLiveData<List<LockAnimation>>()

    private fun getListFrequency() {
        val currentFrequency = preference.getLockFrequencyType()
        val listCommonSelector = arrayListOf<CommonSelector>()
        listCommonSelector.addAll(LockFrequencyType.values().toList().map {
            Frequency(
                getNameLockFrequency(it.id),
                context.getString(getNameLockFrequency(it.id)),
                it.id,
                false
            )
        })

        listCommonSelector.find { it.value == currentFrequency }?.isSelected = true
        listLockFrequencyLiveData.postValue(listCommonSelector)
    }

    private fun getListAnimation() {
        val currentFrequency = preference.getAnimation()
        val listAnimation = arrayListOf<LockAnimation>()
        listAnimation.addAll(LockAnimationType.values().toList().map {
            LockAnimation(
                idString = getNameAnimation(it.id),
                name = context.getString(getNameAnimation(it.id)),
                value = it.id,
                isSelected = false
            )
        })
        listAnimation.find { it.value == currentFrequency }?.isSelected = true
        listAnimationLiveData.postValue(listAnimation)
    }

    fun saveFrequencyToLocal(commonSelector: CommonSelector) {
        preference.setLockFrequencyType(commonSelector.value)
    }

    fun saveAnimationToLocal(commonSelector: CommonSelector) {
        preference.setAnimation(commonSelector.value)
    }

    init {
        getListFrequency()
        getListAnimation()
    }
}