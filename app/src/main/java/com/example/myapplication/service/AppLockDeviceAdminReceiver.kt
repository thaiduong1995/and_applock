package com.example.myapplication.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.util.Log

open class AppLockDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d("Thinhvh32", "Enabled")
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        return "Admin disable requested"
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.i("Thinhvh32 ", "Disabled")
    }

    override fun onPasswordChanged(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordChanged(context, intent, user)
        Log.i("Thinhvh32 ", "Password changed")
    }
}