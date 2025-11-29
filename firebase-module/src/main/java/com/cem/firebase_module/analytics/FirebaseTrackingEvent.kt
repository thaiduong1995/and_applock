package com.cem.firebase_module.analytics

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseTrackingEvent @Inject constructor(
    private val context: Context
) : CemEventTracking {

    private val firebaseEvent by lazy {
        FirebaseAnalytics.getInstance(context)
    }

    override fun logEvent(eventName: String, params: HashMap<String, String>?) {
        val bundle = Bundle()

        params?.entries?.forEach { data ->
            bundle.putString(data.key, data.value)
        }
        firebaseEvent.logEvent(eventName, bundle)
    }

    override fun logEventView(screenName: String) {
        firebaseEvent.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(FirebaseAnalytics.Event.SCREEN_VIEW to screenName)
        )
    }
    companion object{
        @SuppressLint("StaticFieldLeak")
        private var mInstance: FirebaseTrackingEvent? = null

        fun getInstance(activity: Context): FirebaseTrackingEvent {
            return mInstance ?: synchronized(this) {
                val instance = FirebaseTrackingEvent(activity)
                mInstance = instance
                instance
            }
        }
    }
}