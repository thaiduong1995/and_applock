package com.cem.firebase_module.config

import kotlinx.coroutines.flow.Flow

class RemoteConfigImpl : RemoteConfigInterface {
    private val firebaseConfig by lazy {
        MyRemoteConfig()
    }

    override fun getString(value: String): String? {
        return firebaseConfig.getString(value)
    }

    override fun getLong(value: String): Long? {
        return firebaseConfig.getLong(value)
    }

    override fun getBoolean(value: String): Boolean? {
        return firebaseConfig.getBoolean(value)
    }

    override fun getDouble(value: String): Double? {
        return firebaseConfig.getDouble(value)
    }

    override fun fetchConfig(): Flow<Boolean> {
        return firebaseConfig.fetchConfig()
    }
}