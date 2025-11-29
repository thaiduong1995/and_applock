package com.cem.firebase_module.notification

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cem.firebase_module.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        message.let {
            val data = it.notification
            sendNotification(data)
        }
    }

    private fun sendNotification(messageBody: RemoteMessage.Notification?) {

        val intent = Intent(this, getLauncherActivity(this))
        intent.action = "NOTIFICATION"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val channelId = this.applicationContext.packageName
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_black)
            .setContentTitle(if (messageBody?.title.isNullOrEmpty()) "New Message" else messageBody?.title)
            .setContentText(messageBody?.body).setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).setNumber(1)
            .setContentIntent(pendingIntent)
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "User Setting", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun getLauncherActivity(context: Context): Class<*>? {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        if (intent != null && intent.component != null) {
            try {
                return Class.forName(intent.component!!.className) as Class<*>
            } catch (e: ClassNotFoundException) {
                Log.d(
                    TAG,
                    "Failed when resolving the restart activity class via getLaunchIntentForPackage, stack trace follows!",
                    e
                )
            }
        }
        return null
    }

    companion object {
        val TAG: String? = FirebaseMessageService::class.java.simpleName
    }

}