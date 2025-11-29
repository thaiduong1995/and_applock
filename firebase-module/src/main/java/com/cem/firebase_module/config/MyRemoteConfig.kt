package com.cem.firebase_module.config

import android.util.Log
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MyRemoteConfig : RemoteConfigInterface {
    private val remoteConfig by lazy { FirebaseRemoteConfig.getInstance() }

    override fun getString(value: String): String? = remoteConfig.getString(value)

    override fun getLong(value: String): Long? = remoteConfig.getLong(value)

    override fun getBoolean(value: String): Boolean? = remoteConfig.getBoolean(value)

    override fun getDouble(value: String): Double? = remoteConfig.getDouble(value)

    override fun fetchConfig(): Flow<Boolean> = callbackFlow {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 10L else
                3600L
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnSuccessListener {
            Log.e("RemoteConfigViewModel", "addOnSuccessListener: $it")
            trySend(true)
            channel.close()
        }.addOnFailureListener {
            Log.e("RemoteConfigViewModel", "addOnFailureListener: ${it.message}")
            trySend(true)
            channel.close()
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                val updated = it.result
                Log.e("addOnCompleteListener", "$updated")
            }
            trySend(true)
            channel.close()
        }
        awaitClose {
            channel.close()
        }
    }
}