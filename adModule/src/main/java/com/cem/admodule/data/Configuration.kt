package com.cem.admodule.data


data class Configuration(
    val testDeviceIds: List<String> = emptyList()
) {
    var mintegralConfig: MintegralData? = null
        private set
    var useDebugConfig: Boolean = false
        private set

    fun isEnableMintegral() = mintegralConfig != null

    fun enableMintegral(config: MintegralData) = apply {
        mintegralConfig = config
    }

    fun useDebugConfig(useDebug: Boolean) = apply { useDebugConfig = useDebug }

    fun isUseDebugConfig() = useDebugConfig
}