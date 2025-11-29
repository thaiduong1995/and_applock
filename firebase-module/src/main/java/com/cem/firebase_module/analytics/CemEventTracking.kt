package com.cem.firebase_module.analytics

interface CemEventTracking {
    fun logEvent(eventName: String,params :HashMap<String,String>?=null)

    fun logEventView(screenName: String)
}