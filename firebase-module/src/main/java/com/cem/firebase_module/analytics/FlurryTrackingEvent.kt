package com.cem.firebase_module.analytics

import com.flurry.android.FlurryAgent
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FlurryTrackingEvent @Inject constructor(
) : CemEventTracking {
    override fun logEvent(eventName: String, params: HashMap<String, String>?) {
        FlurryAgent.logEvent(eventName, params ?: emptyMap())
    }

    override fun logEventView(screenName: String) {
        FlurryAgent.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            hashMapOf(FirebaseAnalytics.Event.SCREEN_VIEW to screenName)
        )
    }

    companion object {

        private var mInstance: FlurryTrackingEvent? = null
        fun getInstance(): FlurryTrackingEvent {
            return mInstance ?: synchronized(this) {
                val instance = FlurryTrackingEvent()
                mInstance = instance
                instance
            }
        }
    }
}