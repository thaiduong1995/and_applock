package com.cem.admodule.manager

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import com.cem.admodule.inter.BannerAdListener

class CemBannerReloadManager private constructor() {
    private val cemBannerManager by lazy {
        CemBannerManager.getInstance()
    }

    private val handlerListBanner: HashMap<String, Handler> = HashMap()

    private fun getOrPutHandler(configKey: String): Handler {
        val handler: Handler = handlerListBanner.getOrPut(configKey) {
            Handler(Looper.getMainLooper())
        }
        return handler
    }

    fun loadBannerShowNoCollapsibleReload(
        context: Context,
        viewGroup: ViewGroup,
        configKey: String,
        callback: BannerAdListener? = null,
        refreshBannerTime: Int = 10
    ) {
        cemBannerManager.loadBannerShowNoCollapsible(
            context = context,
            viewGroup = viewGroup,
            configKey = configKey,
            callback = callback
        )
        getOrPutHandler(configKey).apply {
            removeCallbacksAndMessages(null)
            postDelayed({
                loadBannerShowNoCollapsibleReload(
                    context, viewGroup, configKey, callback, refreshBannerTime
                )
            }, refreshBannerTime * 1000L)
        }
    }

    fun loadAndShowBannerByContextReload(
        context: Context,
        viewGroup: ViewGroup,
        configKey: String,
        position: String? = null,
        callback: BannerAdListener? = null,
        refreshBannerTime: Int = 10
    ) {
        cemBannerManager.loadAndShowBannerByContext(
            context = context,
            viewGroup = viewGroup,
            configKey = configKey,
            position = position,
            callback = callback
        )

        getOrPutHandler(configKey).apply {
            removeCallbacksAndMessages(null)
            postDelayed({
                loadAndShowBannerByContextReload(
                    context,
                    viewGroup,
                    configKey,
                    position,
                    callback,
                    refreshBannerTime
                )
            }, refreshBannerTime * 1000L)
        }
    }

    fun loadBannerAndShowByActivityReload(
        activity: Activity,
        viewGroup: ViewGroup,
        configKey: String,
        position: String? = null,
        callback: BannerAdListener? = null,
        refreshBannerTime: Int = 10
    ) {
        cemBannerManager.loadBannerAndShowByActivity(
            activity = activity,
            viewGroup = viewGroup,
            configKey = configKey,
            position = position,
            callback = callback
        )

        getOrPutHandler(configKey).apply {
            removeCallbacksAndMessages(null)
            postDelayed({
                loadBannerAndShowByActivityReload(
                    activity,
                    viewGroup,
                    configKey,
                    position,
                    callback,
                    refreshBannerTime
                )
            }, refreshBannerTime * 1000L)
        }
    }

    fun removeRunnableAndCallback(configKey: String, messages: String? = null) {
        getOrPutHandler(configKey).removeCallbacksAndMessages(messages)
    }

    companion object {
        val TAG = CemBannerReloadManager::class.java.simpleName

        private var mInstance: CemBannerReloadManager? = null

        fun getInstance(): CemBannerReloadManager {
            return mInstance ?: synchronized(this) {
                val instance = CemBannerReloadManager()
                mInstance = instance
                instance
            }
        }
    }
}