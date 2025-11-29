package  com.example.myapplication.extention

import android.app.Activity
import android.content.Intent
import android.content.res.TypedArray
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Size
import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

val Activity.statusBarHeight: Int
    get() {
        var statusBarHeight = 0
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

val Activity.getNavigationBarHeight: Int
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


val Activity.actionBarHeight: Int
    get() {
        var actionBarHeight = 0
        val styledAttributes: TypedArray = theme.obtainStyledAttributes(
            intArrayOf(
                com.google.android.material.R.attr.actionBarSize
            )
        )
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return actionBarHeight
    }

val Activity.realScreenSize: Size
    get() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return Size(width, height)
    }

val Activity.screenSize: Size
    get() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return Size(width, height)
    }
val Activity.density: Float
    get() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.density
    }


fun AppCompatActivity.showHowToUseDialog(onDismissListener: () -> Unit) {
//    val dialog = HowToUseDialog.newInstance()
//    dialog.setOnDismissListener {
//        onDismissListener()
//    }
//    dialog.show(supportFragmentManager,null)
}

fun Activity.showRateApp() {
//    val tag = "GOOGLE RATE APP"
//    val reviewManager = ReviewManagerFactory.create(this)
//    val request: Task<ReviewInfo> = reviewManager.requestReviewFlow()
//    request.addOnCompleteListener { task ->
//        if (task.isSuccessful) {
//            Log.e(tag, "showRateApp -> request isSuccessful")
//            // We can get the ReviewInfo object
//            val reviewInfo: ReviewInfo = task.result
//            val flow: Task<Void> = reviewManager.launchReviewFlow(this, reviewInfo)
//            flow.addOnCompleteListener { task1 ->
//                RateAppSharePref.getInstance(this).canShowRateApp = true
//                RateAppSharePref.getInstance(this).incrementNumberSavedSlideShow()
//            }
//        } else {
//            Log.e(tag, "showRateApp -> request fail")
//            // There was some problem, continue regardless of the result.
//            // show native rate app dialog on error
////                showRateAppFallbackDialog()
//        }
//    }

//    fun Activity.haveStoragePermission(): Boolean {
//        return if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED
//            ) {
//                true
//            } else {
////                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
//                false
//            }
//        } else {
//            true
//        }
//    }
}

fun Activity.isHaveBiometric(): Boolean {
    val biometricManager = BiometricManager.from(this)
    when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            return true
        }

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            return false
        }

        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            return false
        }

        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
            }
            return false
        }

        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
            return false
        }

        else -> return false
    }
}

fun Activity.isHaveFingerPrint(): Boolean {
    val fingerprintManagerCompat = FingerprintManagerCompat.from(this)
    return if (!fingerprintManagerCompat.isHardwareDetected) {
        false
    } else fingerprintManagerCompat.hasEnrolledFingerprints()
}