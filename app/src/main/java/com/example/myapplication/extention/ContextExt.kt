package  com.example.myapplication.extention

import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myapplication.R
import com.example.myapplication.data.model.LockAnimationType
import com.example.myapplication.data.model.LockFrequencyType
import java.io.ByteArrayOutputStream

fun Context.dp2px(dp: Float): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.shareMyApp(appName: String, appId: String) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName)
        val shareMessage =
            """https://play.google.com/store/apps/details?id=$appId""".trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Choose one"))
    } catch (e: Exception) {
        //e.toString();
        e.printStackTrace()
    }
}

fun Context.shareTextOnly(message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND)

    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
    shareIntent.putExtra(Intent.EXTRA_TEXT, message)
    startActivity(Intent.createChooser(shareIntent, "Share Via"))
}

fun Context.rateMyApp(appId: String) {
    val uri: Uri = Uri.parse("market://details?id=$appId")
    val launchIntent = Intent(Intent.ACTION_VIEW, uri)
    launchIntent.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    )
    try {
        startActivity(launchIntent)
    } catch (ex: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=$appId")
            )
        )
    }
}

fun Context.support(appName: String, emailAddress: String) {
    val mailIntent = Intent(Intent.ACTION_SEND)
    val data =
        Uri.parse("mailto:?SUBJECT=$appName&body=&to=$emailAddress")
    mailIntent.data = data
    startActivity(Intent.createChooser(mailIntent, "Send mail"))
}

val Context.navigationBarHeight: Int
    get() {
        if (KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)) {
            return 0
        }
        var navigationBarHeight = 0
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return navigationBarHeight
    }

fun Context.toastMessageShortTime(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toastMessageLongTime(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.sendLocalBroadcast(intent: Intent) {
    val localBroadcastManager = LocalBroadcastManager.getInstance(this)
    localBroadcastManager.sendBroadcast(intent)
}

fun Context.unregisterLocalBroadcast(broadcastReceiver: BroadcastReceiver) {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
}

fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun decodeBase64(input: String?): Bitmap? {
    if (input == null)
        return null
    val decodedByte: ByteArray = Base64.decode(input, 0)
    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
}

fun encodeBitmapToString(bitmap: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //
    val b: ByteArray = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

//fun Context.isNetworkAvailable(): Boolean {
//    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//        val nw = connectivityManager.activeNetwork ?: return false
//        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
//        return when {
//            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//            //for other device how are able to connect with Ethernet
//            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//            //for check internet over Bluetooth
//            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
//            else -> false
//        }
//    } else {
//        return connectivityManager.activeNetworkInfo?.isConnected ?: false
//    }
//}
//
//val Context.isWifiOn get() =  with( getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//        getNetworkCapabilities(activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
//    } else {
//        getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.isConnected
//    }
//}
//
//private fun hasNetworkAvailable(context: Context): Boolean {
//    val service = Context.CONNECTIVITY_SERVICE
//    val manager = context.getSystemService(service) as ConnectivityManager?
//    val network = manager?.activeNetworkInfo
//    return (network != null)
//}
//fun Context.haveOverlayPermission(): Boolean {
//    return if (VersionUtils.hasMarshmallow()) {
//        Settings.canDrawOverlays(this.applicationContext)
//    } else {
//        true
//    }
//}
//
//fun AppCompatActivity.requestOverlayPermission(onResult:(Boolean)->Unit) {
//    if(haveOverlayPermission()){
//        onResult.invoke(true)
//    } else{
//        if(VersionUtils.hasMarshmallow()){
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                onResult.invoke(haveOverlayPermission())
//            }.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
//        }
//    }
//}
//
//fun Context.configLanguage() {
//    val config = resources.configuration
//    val lang = SettingsSharedPref.getInstance(this).language.code
//    if (lang.isNotEmpty()) {
//        val locale = Locale(lang)
//
//        Locale.setDefault(locale)
//        config.locale = locale
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//            createConfigurationContext(config)
//        resources.updateConfiguration(config, resources.displayMetrics)
//    }
//}

fun getNameLockFrequency(lockFrequencyValue: Int): Int {
    return when (lockFrequencyValue) {
        LockFrequencyType.ALWAYS.id -> R.string.always
        LockFrequencyType.AFTER_SCREEN_LOCK.id -> R.string.after_screen_lock
        LockFrequencyType.AFTER_SCREEN_LOCK.id -> R.string.after_screen_lock
        LockFrequencyType.TEN_SECONDS.id -> R.string.ten_seconds
        LockFrequencyType.THIRTY_SECONDS.id -> R.string.thirty_seconds
        LockFrequencyType.ONE_MINUTE.id -> R.string.ont_minius
        LockFrequencyType.THREE_MINUTE.id -> R.string.three_minius
        LockFrequencyType.FIVE_MINUTE.id -> R.string.five_minius
        LockFrequencyType.TEN_MINUTE.id -> R.string.ten_minius
        else -> R.string.always
    }
}

fun getNameAnimation(animationValue: Int): Int {
    return when (animationValue) {
        LockAnimationType.SWIPE_TOP.id -> R.string.swipe_top
        LockAnimationType.SWIPE_DOWN.id -> R.string.swipe_down
        LockAnimationType.SWIPE_RIGHT.id -> R.string.swipe_right
        LockAnimationType.SWIPE_LEFT.id -> R.string.swipe_left
        LockAnimationType.FADE_OUT.id -> R.string.swipe_fade_out
        LockAnimationType.RANDOM.id -> R.string.random
        else -> R.string.swipe_top
    }
}


