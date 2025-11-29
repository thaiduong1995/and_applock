package com.example.myapplication.service

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.myapplication.utils.Constants.KEY_BUNDLE

object ServiceStarter {

    fun startService(context: Context) {
        try {
            val intent = Intent(context, AppLockerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun startServiceWithData(context: Context, bundle: Bundle) {
        try {
            val intent = Intent(context, AppLockerService::class.java)
            intent.putExtra(KEY_BUNDLE, bundle)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun restartService(context: Context) {
        try {
            val intent = Intent(context, AppLockerService::class.java)
            context.stopService(intent)
            startService(context)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun stopLockService(context: Context) {
        try {
            val intent = Intent(context, AppLockerService::class.java)
            context.stopService(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun startServiceMain(context: Context) {
        try {
            if (!isMyServiceRunning(context, AppLockerService::class.java)) {
                restartService(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}