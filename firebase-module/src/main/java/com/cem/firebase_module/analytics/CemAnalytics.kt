package com.cem.firebase_module.analytics

import android.content.Context
import android.util.Log

object CemAnalytics {

    fun logEventClickView(context: Context?, screenName:String?,actionName:String) {
        if(context == null || screenName == null) return
        val a = "${screenName}_click_$actionName"
        Log.d("LOG EVENT CLICK","$a - ${a.length}")
        TrackingEventImpl.getInstance(context).logEventView(a)
    }

    fun logEventClickAndParams(context: Context?, screenName:String?,actionName:String, params: HashMap<String, String>? = null) {
        if(context == null || screenName == null) return
        val a = "${screenName}_click_$actionName"
        Log.d("LOG EVENT CLICK AND PARAM","$a - ${a.length}")
        TrackingEventImpl.getInstance(context).logEvent(a, params)
    }

    fun logEventAndParams(context: Context, eventName: String, params: HashMap<String, String>? = null) {
        TrackingEventImpl.getInstance(context).logEvent(eventName, params)
    }

    fun logEventScreenView(context: Context, eventName: String) {
        val a = "${eventName}_show"
        Log.d("LOG EVENT SHOW","$a - ${a.length}")
        TrackingEventImpl.getInstance(context).logEventView(a)
    }
}