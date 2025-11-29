package com.example.myapplication.data.model.liveData

class SingleEvent<T>(private val content: T) {

    private var handled: Boolean = false

    fun getOneTimeContent(): T? {
        return if (handled) {
            null
        } else {
            handled = true
            content
        }
    }
}
