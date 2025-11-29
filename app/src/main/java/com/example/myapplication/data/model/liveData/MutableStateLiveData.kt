package com.example.myapplication.data.model.liveData

import androidx.lifecycle.MutableLiveData

/**
 * Created by Thinhvh on 24/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
class MutableStateLiveData<T> : MutableLiveData<StateData<T>>() {

    fun postLoading() {
        postValue(StateData<T>().loading())
    }

    fun postError(errorMsg: String?) {
        postValue(StateData<T>().error(errorMsg ?: ""))
    }

    fun postErrorData(errorData: T) {
        postValue(StateData<T>().error(errorData))
    }

    fun postSuccess(data: T) {
        postValue(StateData<T>().success(data))
    }
}