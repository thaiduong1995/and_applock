package com.example.myapplication.view_model

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.model.CommonSelector
import com.example.myapplication.data.model.RecommendSignal
import com.example.myapplication.data.model.liveData.MutableStateLiveData
import com.example.myapplication.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FakeCoverViewModel @Inject constructor(
    val preference: PreferenceHelper
) : BaseViewModel() {

    val listRecommendLiveData = MutableStateLiveData<ArrayList<CommonSelector>>()

    fun getRecommendSignal(context: Context) {
        val listRecommend = arrayListOf<CommonSelector>()
        listRecommend.add(
            RecommendSignal(
                context.getString(R.string.your_app_is_stopped),
                resId = R.string.your_app_is_stopped
            )
        )
        listRecommend.add(
            RecommendSignal(
                context.getString(R.string.the_operation_could_not_be_completed),
                resId = R.string.the_operation_could_not_be_completed
            )
        )
        listRecommend.add(
            RecommendSignal(
                context.getString(R.string.error_something_went_wrong),
                resId = R.string.error_something_went_wrong
            )
        )
        listRecommend.add(
            RecommendSignal(
                context.getString(R.string.an_error_occured_while_loading_file),
                resId = R.string.an_error_occured_while_loading_file
            )
        )
        listRecommend.add(
            RecommendSignal(
                context.getString(R.string.connected_lost),
                resId = R.string.connected_lost
            )
        )

        val currentRecommend =
            preference.getRecommendSignals()
        listRecommend.firstOrNull { it.idString == currentRecommend }?.isSelected = true
        listRecommendLiveData.postSuccess(listRecommend)
    }

    fun saveRecommendSignal(name: Int) {
        viewModelScope.launch {
            preference.setRecommendSignals(name)
        }
    }

    fun isFakeCoverEnabled(): Boolean {
        return preference.isFakeCoverEnable()
    }

    fun setEnableFakeCover(enable: Boolean) {
        return preference.setEnableFakeCover(enable)
    }
}