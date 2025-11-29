package com.example.myapplication.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

class LauncherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageName = intent.getStringExtra("pkg") as String
        openApp(this, packageName)
        finish()
    }

    private fun openApp(context: Context?, string: String) {
        if (context == null) return
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(string)
        if (intent != null) {
            val componentName = intent.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            context.startActivity(mainIntent)
        }
    }
}