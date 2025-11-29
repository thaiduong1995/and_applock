package com.cem.admodule.inter

interface Callback<T> {
    fun onSuccess(data :  T)

    fun onFailure(e : Exception?)
}