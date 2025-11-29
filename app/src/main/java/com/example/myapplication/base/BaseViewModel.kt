package com.example.myapplication.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

/**
 * Created by Thinhvh on 23/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
open class BaseViewModel : ViewModel() {

    var isLoading = MutableLiveData<Boolean>()
    var showMessageLiveData = MutableLiveData<String>()
    var parentJob: Job? = null

    protected fun registerJobFinish() {
        parentJob?.invokeOnCompletion {
            showLoading(false)
        }
    }

    protected fun showLoading(isShow: Boolean) {
        isLoading.postValue(isShow)
    }

    protected fun showMessage(message: String) {
        showMessageLiveData.postValue(message)
    }
}