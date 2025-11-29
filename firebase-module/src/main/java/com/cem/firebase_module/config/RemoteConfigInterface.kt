package com.cem.firebase_module.config

import kotlinx.coroutines.flow.Flow


interface RemoteConfigInterface {
    fun getString(value: String): String?

    fun getLong(value: String): Long?

    fun getBoolean(value: String): Boolean?

    fun getDouble(value: String): Double?

    fun fetchConfig(): Flow<Boolean>
}