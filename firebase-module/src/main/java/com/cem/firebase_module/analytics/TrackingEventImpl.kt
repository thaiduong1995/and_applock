package com.cem.firebase_module.analytics

import android.annotation.SuppressLint
import android.content.Context
import javax.inject.Inject

class TrackingEventImpl @Inject constructor(
    private val context: Context
) : CemEventTracking {

    private val firebaseTrackingEvent by lazy {
        FirebaseTrackingEvent.getInstance(context)
    }

    private val flurryTrackingEvent by lazy {
        FlurryTrackingEvent.getInstance()
    }

    override fun logEvent(eventName: String, params: HashMap<String, String>?) {
        firebaseTrackingEvent.logEvent(eventName, params)
        flurryTrackingEvent.logEvent(eventName, params)
    }

    override fun logEventView(screenName: String) {
        firebaseTrackingEvent.logEventView(screenName)
        flurryTrackingEvent.logEventView(screenName)
    }

    companion object{
        @SuppressLint("StaticFieldLeak")
        private var mInstance: TrackingEventImpl? = null

        fun getInstance(activity: Context): TrackingEventImpl {
            return mInstance ?: synchronized(this) {
                val instance = TrackingEventImpl(activity)
                mInstance = instance
                instance
            }
        }
    }
}