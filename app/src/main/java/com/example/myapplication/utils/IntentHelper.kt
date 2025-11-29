package com.example.myapplication.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.example.myapplication.BuildConfig
import com.example.myapplication.R

object IntentHelper {

    const val EMAIL = "cemsoftware.contact@gmail.com"
    const val URL_POLICY =
        "https://docs.google.com/document/d/1P0YtI7JIiDEgmfqKS_lTcTXnBvrvfJltEcskwt6YA7U/edit"
    const val URL_TERM =
        "https://docs.google.com/document/d/1P0YtI7JIiDEgmfqKS_lTcTXnBvrvfJltEcskwt6YA7U/edit"

    fun launcherIntent(): Intent {
        val intent = Intent("android.intent.action.MAIN")
        intent.addCategory("android.intent.category.HOME")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }

    fun overlayIntent(packageName: String): Intent {
        return Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
    }

    fun usageAccessIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }

    fun privacyPolicyWebIntent(): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(URL_POLICY))
    }

    fun privacyTermWebIntent(): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(URL_TERM))
    }

    fun rateUsIntent(context: Context): Intent {
        return Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
        )
    }

    fun FacebookIntent(context: Context): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Applockpro"))
    }

    fun YoutubeIntent(context: Context): Intent {
        return Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/channel/UCb0HKc3e8I6AqQJw08MqAeg")
        )
    }

    fun TiktokIntent(context: Context): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@applock_app"))
    }


    fun shareIntent(context: Context): Intent {
        val appPackageName = BuildConfig.APPLICATION_ID
        val appName = context.getString(R.string.app_name)
        val appDecs = context.getString(R.string.app_decs)
        val shareBodyText =
            "$appDecs \n https://play.google.com/store/apps/details?id=$appPackageName"

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, appName)
            putExtra(Intent.EXTRA_TEXT, shareBodyText)
        }
        return Intent.createChooser(sendIntent, null)
    }

    fun sendMailIntent(): Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL))
        return intent
    }

    //test auto accept permission xiaomi
//    Intent rIntent = context.getPackageManager()
//    .getLaunchIntentForPackage(context.getPackageName() );
//    PendingIntent intent = PendingIntent.getActivity(
//    context, 0,
//    rIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//    manager.set(AlarmManager.RTC, System.currentTimeMillis(), intent);
//    System.exit(2);
    //refer https://github.com/zoontek/react-native-permissions/issues/412

//    https://stackoverflow.com/questions/59418504/xiaomi-devices-permission-to-enable-apps-pop-up-windows-while-running-in-the-bac
    // google play sẽ tự động accept quyền này nếu ứng dụng hướng đến api >30

    fun pickImageIntent(): Intent {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        return intent
    }
}