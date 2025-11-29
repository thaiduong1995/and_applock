package com.cem.admodule.manager

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

/**
 * Created by Hưng Nguyễn on 26/12/2023
 * Phone: 0335236374
 * Email: nguyenhunghung2806@gmail.com
 */
class GoogleConsentManager private constructor(context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    fun interface OnConsentGatheringCompleteListener {
        fun consentGatheringComplete(error: FormError?)
    }

    /** Helper variable to determine if the app can request ads. */
    val isRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    /** Helper variable to determine if the privacy options form is required. */
    val isPrivacyOptions: Boolean
        get() =
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /**
     * Helper method to call the UMP SDK methods to request consent information and load/show a
     * consent form if necessary.
     */
    fun gatherConsentDebugWithGeography(
        activity: Activity,
        geography: Int,
        hashedId: String? = null,
        appId: String? = null,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener
    ) {
        //the below code is for testing purpose only. Remove it in production build.
        val debugSettings =
            ConsentDebugSettings.Builder(activity)
                .setDebugGeography(geography)
        hashedId?.let {
            debugSettings.addTestDeviceHashedId(it)
        }
        //set tag for under age of consent
        //request consent
        val params =
            ConsentRequestParameters.Builder()
                .setConsentDebugSettings(debugSettings.build())
        appId?.let {
            params.setAdMobAppId(it)
        }
        requestConsentInfoUpdate(
            activity = activity,
            params = params.build(),
            onConsentGatheringCompleteListener = onConsentGatheringCompleteListener
        )
    }

    fun gatherConsent(
        activity: Activity,
        underAge: Boolean = false,
        appId: String? = null,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener
    ) {
        val params = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(underAge)
        appId?.let {
            params.setAdMobAppId(it)
        }
        requestConsentInfoUpdate(
            activity = activity,
            params = params.build(),
            onConsentGatheringCompleteListener = onConsentGatheringCompleteListener
        )
    }

    private fun requestConsentInfoUpdate(
        activity: Activity,
        params: ConsentRequestParameters,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener
    ) {
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            { // The consent information state was updated, ready to check if a form is available.
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                    onConsentGatheringCompleteListener.consentGatheringComplete(error)
                }
            },
            { formError ->
                onConsentGatheringCompleteListener.consentGatheringComplete(formError)
            }
        )
    }

    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
    }

    fun resetConsent(){
        consentInformation.reset()
    }

    companion object {
        @Volatile
        private var instance: GoogleConsentManager? = null

        fun getInstance(context: Context) =
            instance
                ?: synchronized(this) {
                    instance ?: GoogleConsentManager(context).also { instance = it }
                }
    }
}