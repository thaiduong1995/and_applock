package com.example.myapplication.ui.activities

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.drawable.ColorDrawable
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.data.model.AppData
import com.example.myapplication.data.model.IndicatorType
import com.example.myapplication.data.model.Intruder
import com.example.myapplication.data.model.LockAnimationType
import com.example.myapplication.data.model.OverlayValidateType
import com.example.myapplication.data.model.ThemeData
import com.example.myapplication.data.model.liveData.StateData
import com.example.myapplication.databinding.ActivityLockBinding
import com.example.myapplication.extention.gone
import com.example.myapplication.extention.isHaveBiometric
import com.example.myapplication.extention.isHaveFingerPrint
import com.example.myapplication.extention.visible
import com.example.myapplication.service.ServiceStarter
import com.example.myapplication.ui.custom.LockOverlayView
import com.example.myapplication.ui.dialog.DialogConfirmForgotPassword
import com.example.myapplication.ui.dialog.DialogForgotPassword
import com.example.myapplication.ui.dialog.fingerprint.EasyFingerPrint
import com.example.myapplication.ui.fragment.CreatePasswordFragment
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Constants.KEY_PACKAGE_NAME
import com.example.myapplication.utils.Constants.KEY_UNLOCK_SUCCESS
import com.example.myapplication.utils.FileUtil
import com.example.myapplication.utils.IntentHelper
import com.example.myapplication.utils.PermissionChecker
import com.example.myapplication.utils.PreferenceHelper
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.sensorToDeviceRotation
import com.example.myapplication.view_model.LockOverlayViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.Random
import javax.inject.Inject

@AndroidEntryPoint
class LockActivity : BaseActivity(), View.OnClickListener {

    @Inject
    lateinit var preferences: PreferenceHelper

    private lateinit var binding: ActivityLockBinding
    private val viewModel by viewModels<LockOverlayViewModel>()
    private var currentApp: AppData? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null
    private var imageFilePath: String? = null

    //camera
    private var backgroundHandlerThread: HandlerThread? = null

    private var backgroundHandler: Handler? = null
    private var mCameraDevice: CameraDevice? = null
    private var previewRequestBuilder: CaptureRequest.Builder? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mCameraID: String? = null
    private var mImageReader: ImageReader? = null
    private var mCameraManager: CameraManager? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private var isActivityShowing = false
    private var isStartMain = true
    private var isUnLock = false
    private var triesCountError = 0
    private var triesCountMax = 0
    private var triesCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.transparent)
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        initUI()
        getDataFromIntent()
        initObserve()
        initListener()
        checkFingerPrint()
        initLockOverlayView()
    }

    private fun getData() {
        if (System.currentTimeMillis() - preferences.getTimeUnLock() >= AlarmManager.INTERVAL_HOUR * 2) {
            triesCountMax = if (preferences.getLockFirst()) 3 else 5
        }
    }

    override fun initUI() {
        val themeId = preferences.getThemeId()
        val lockType = OverlayValidateType.entries.find {
            it.value == preferences.getLockType()
        } ?: OverlayValidateType.TYPE_PATTERN
        var backgroundPath: String? = when (lockType.value) {
            OverlayValidateType.TYPE_PATTERN.value -> {
                Utils.getAssetPath(themeId).plus(ThemeData.PATTERN_BACKGROUND)
            }

            OverlayValidateType.TYPE_KNOCK_CODE.value -> {
                Utils.getAssetPath(themeId).plus(ThemeData.KNOCK_BACKGROUND)
            }

            OverlayValidateType.TYPE_PIN.value -> {
                Utils.getAssetPath(themeId).plus(ThemeData.PIN_BACKGROUND)
            }

            else -> null
        }
        backgroundPath?.let {
            Glide.with(this).load(Uri.parse(Constants.ASSET_PATH.plus(it))).into(binding.bgView)
        }

        // check xem theme hien tai la default hay khong va current custom theme co ton tai khong
        viewModel.getCurrentCustomTheme()?.let {
            if (lockType.value == it.lockType && themeId == Constants.DEFAULT_THEME) {
                backgroundPath = it.backgroundImagePath
                Glide.with(this).load(backgroundPath).into(binding.bgView)
            }
        }

        if (preferences.getSecurityQuestionEnable() && preferences.getSecurityQuestionId() != 0) {
            binding.tvForgotPassword.visibility = View.VISIBLE
        } else {
            binding.tvForgotPassword.visibility = View.INVISIBLE
        }

        binding.imgTheme.gone()
    }

    private fun registerGlobalLayoutListener() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                isActivityShowing = true
                initCamera()
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun initCamera() {
        if (
            preferences.isCaptureIntrudersEnabled().not()
            && !PermissionChecker.isPermissionCameraEnable(this)
        ) return

        mSurfaceHolder = binding.textureView.holder
        mSurfaceHolder?.setKeepScreenOn(true)
        mSurfaceHolder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                initCamera2()
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                if (null != mCameraDevice) {
                    mCameraDevice?.close()
                    mCameraDevice = null
                }
            }
        })
    }

    private fun initCamera2() {
        startBackgroundThread()
        mCameraID = "" + CameraCharacteristics.LENS_FACING_BACK // Read camera
        mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1)
        mImageReader?.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler)
        openCamera2View()
    }

    private fun openCamera2View() {
        mCameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                mCameraManager?.openCamera(mCameraID ?: return, stateCallback, backgroundHandler)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            takePreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            if (null != mCameraDevice) {
                mCameraDevice?.close()
                mCameraDevice = null
            }
        }

        override fun onError(camera: CameraDevice, error: Int) {
            if (null != mCameraDevice) {
                mCameraDevice?.close()
                mCameraDevice = null
            }
        }
    }

    private fun takePreview() {
        try {
            previewRequestBuilder =
                mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mSurfaceHolder?.surface?.let {
                previewRequestBuilder?.addTarget(it)
            }
            mCameraDevice?.createCaptureSession(
                listOf(mSurfaceHolder?.surface, mImageReader?.surface),
                previewStateCallback,
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val previewStateCallback: CameraCaptureSession.StateCallback =
        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                if (null == mCameraDevice) return
                mCameraCaptureSession = cameraCaptureSession
                try {
                    previewRequestBuilder?.let { builder ->
                        builder.set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        )
                        val previewRequest = builder.build()
                        mCameraCaptureSession?.setRepeatingRequest(
                            previewRequest, null, backgroundHandler
                        )
                    }

                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {}

            override fun onClosed(session: CameraCaptureSession) {
                super.onClosed(session)
                stopBackgroundThread()
            }
        }

    private val onImageAvailableListener = object : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader) {
            var image: Image? = null
            try {
                image = reader.acquireLatestImage()
                val buffer: ByteBuffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.capacity())
                buffer.get(bytes)
                save(bytes)
                saveImageToDB()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                image?.close()
            }
        }

        private fun save(bytes: ByteArray) {
            var output: OutputStream? = null
            try {
                val imgFile = FileUtil.createFile(this@LockActivity)
                imageFilePath = imgFile.absolutePath
                output = FileOutputStream(imgFile)
                output.write(bytes)
            } finally {
                output?.close()
            }
        }
    }

    private fun initLockOverlayView() {
        (System.currentTimeMillis() - preferences.getTimeUnLock() >= AlarmManager.INTERVAL_HOUR * 2).let {
            if (it) binding.lockOverlayDisable.gone() else {
                binding.tvUnlockStatus.text = getString(R.string.try_again_after_2_hours)
                binding.lockOverlayDisable.visible()
            }

        }
        binding.lockOverlay.setCallback(object : LockOverlayView.OverlayCallback {
            override fun onStartInput() {
                binding.tvUnlockStatus.isVisible = false
            }

            override fun onUnlockSuccess() {
                unLockApp()
            }

            override fun onUnLockError(msg: String) {
                vibrator()
                binding.tvUnlockStatus.text = msg
                binding.tvUnlockStatus.isVisible = true

                triesCount++
                if (preferences.isCaptureIntrudersEnabled() && triesCount >= preferences.getUnlockCount()) {
                    takePhoto()
                }
                lockUpError()
            }

            override fun onInputting() {
                vibrator()
            }
        })

        binding.lockOverlay.setTheme(preferences.getThemeId())
        viewModel.getCurrentCustomTheme()?.let {
            binding.lockOverlay.setCustomTheme(it)
        }
        binding.lockOverlay.setIndicatorType(IndicatorType.UNLOCK)
        binding.lockOverlayDisable.setOnClickListener {

        }
    }

    private fun vibrator() {
        if (preferences.isVibrationEnable()) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        200, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator?.vibrate(200)
            }
        }
    }

    private fun takePhoto() {
        if (mCameraDevice == null) return
        val captureRequestBuilder: CaptureRequest.Builder
        try {
            captureRequestBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureRequestBuilder.addTarget(mImageReader!!.surface)
            captureRequestBuilder.set(
                CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )

            val rotation = windowManager.defaultDisplay.rotation
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mCameraID?.let { it ->
                mCameraManager?.getCameraCharacteristics(it)?.let { cameraCharacteristics ->
                    sensorToDeviceRotation(cameraCharacteristics, rotation)
                }
            })
            val mCaptureRequest = captureRequestBuilder.build()
            mCameraCaptureSession!!.capture(mCaptureRequest, null, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun unLockApp() {
        cemAdManager.showInterstitialCallback(
            activity = this,
            configKey = ConstAd.FULL_KEY_DETAIL,
            callback = {
                isUnLock = true
                IS_SHOWING = false
                binding.textureView.gone()
                val idAnimation = preferences.getAnimation()
                if (currentApp?.packageName == this@LockActivity.packageName && isStartMain) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                finishWithAnimation(idAnimation)
                resetUnLockApp()
            }
        )
    }

//    private fun startActivityWithAnimation(animId: Int) {
//        if (animId == LockAnimationType.RANDOM.id) {
//            startActivityWithAnimation(Random().nextInt(4))
//            return
//        }
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finishActivity()
//        when (animId) {
//            LockAnimationType.SWIPE_TOP.id -> {
//                overridePendingTransition(android.R.anim.slide_in_left, R.anim.swipe_top)
//            }
//            LockAnimationType.SWIPE_DOWN.id -> {
//                overridePendingTransition(android.R.anim.slide_in_left, R.anim.swipe_down)
//            }
//
//            LockAnimationType.SWIPE_RIGHT.id -> {
//                overridePendingTransition(
//                    android.R.anim.slide_in_left, android.R.anim.slide_out_right
//                )
//            }
//            LockAnimationType.SWIPE_LEFT.id -> {
//                overridePendingTransition(R.anim.slide_in_from_right, R.anim.swipe_left)
//            }
//            LockAnimationType.FADE_OUT.id -> {
//                overridePendingTransition(0, R.anim.fade_out)
//            }
//        }
//    }

    private fun finishWithAnimation(animId: Int) {
        when (animId) {
            LockAnimationType.SWIPE_TOP.id -> {
                binding.rootView.animate().setDuration(DURATION)
                    .translationY(-binding.root.measuredHeight.toFloat()).setListener(animCallback)
                    .start()
            }

            LockAnimationType.SWIPE_DOWN.id -> {
                binding.rootView.animate().setDuration(DURATION)
                    .translationY(binding.root.measuredHeight.toFloat()).setListener(animCallback)
                    .start()
            }

            LockAnimationType.SWIPE_RIGHT.id -> {
                binding.rootView.animate().setDuration(DURATION)
                    .translationX(binding.root.measuredWidth.toFloat()).setListener(animCallback)
                    .start()
            }

            LockAnimationType.SWIPE_LEFT.id -> {
                binding.rootView.animate().setDuration(DURATION)
                    .translationX(-binding.root.measuredWidth.toFloat()).setListener(animCallback)
                    .start()
            }

            LockAnimationType.FADE_OUT.id -> {
                if (currentApp?.packageName == this.packageName) {
                    overridePendingTransition(0, R.anim.fade_out)
                    finishActivity()
                } else {
                    binding.rootView.alpha = 1f
                    binding.rootView.animate().alpha(0f).setDuration(DURATION)
                        .setListener(animCallback).start()
                }
            }

            LockAnimationType.RANDOM.id -> {
                finishWithAnimation(Random().nextInt(4))
            }
        }
    }

    private fun checkFingerPrint() {
        val isDeviceSupportBiometrics = isHaveBiometric()
        val isDeviceSupportFingerPrint = isHaveFingerPrint()

        val isHaveCondition =
            (isDeviceSupportBiometrics && viewModel.preference.isEnableFingerPrint())
                    || (isDeviceSupportFingerPrint && viewModel.preference.isEnableFingerPrint())

        binding.imgFingerPrint.isVisible = isHaveCondition
        if (isDeviceSupportBiometrics) {
            initBiometric()
        }
    }

    private fun initObserve() {
        viewModel.appLiveData.observe(this) {
            when (it.getStatus()) {
                StateData.DataStatus.LOADING -> {}

                StateData.DataStatus.SUCCESS -> {
                    initViewWithData(it.getData())
                }

                StateData.DataStatus.ERROR -> {}

                else -> {}
            }
        }
    }

    private fun initBiometric() {
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    unLockApp()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    lockUpError()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    lockUpError()
                }
            })
        promptInfo =
            BiometricPrompt.PromptInfo.Builder().setTitle(getString(R.string.use_biometric_label))
                .setSubtitle(getString(R.string.biometric_prompt_message))
                .setNegativeButtonText(getString(R.string.confirm_device_credential_password))
                .build()
    }

    override fun initListener() {
        binding.apply {
            imgFingerPrint.setOnClickListener(this@LockActivity)
            root.setOnClickListener(this@LockActivity)
            tvForgotPassword.setOnClickListener(this@LockActivity)
        }
    }

    private fun initViewWithData(data: AppData?) {
        this.currentApp = data
        currentApp?.let {
            Glide.with(this).load("pkg:".plus(it.packageName)).error(
                ColorDrawable(
                    ContextCompat.getColor(
                        this, R.color.grayC4CDE2
                    )
                )
            ).into(binding.imgAppIcon)
            binding.tvAppName.text = it.appName
        }
    }

    private fun getDataFromIntent() {
        val currentAppPackageName = intent.getStringExtra(KEY_PACKAGE_NAME)
        isStartMain = intent.getBooleanExtra(START_MAIN, true)
        currentAppPackageName?.let {
            viewModel.getAppData(currentAppPackageName)
        }
    }

    private fun showFingerprintDialog() {
        EasyFingerPrint(this).setListern(object : EasyFingerPrint.ResultFingerPrintListern {
            override fun onError(mensage: String, code: Int) {
                Toast.makeText(
                    this@LockActivity, getString(R.string.fingerprint_error), Toast.LENGTH_SHORT
                ).show()
            }

            override fun onSucess(cryptoObject: FingerprintManagerCompat.CryptoObject?) {
                unLockApp()
            }

            override fun onFailed() {
                lockUpError()
            }
        }).startScan()
    }

    private fun startBackgroundThread() {
        backgroundHandlerThread = HandlerThread("CameraVideoThread")
        backgroundHandlerThread?.start()
        backgroundHandlerThread?.let {
            backgroundHandler = Handler(it.looper)
        }
    }

    private fun stopBackgroundThread() {
        try {
            backgroundHandlerThread?.let { thread ->
                thread.quitSafely()
                thread.join()
            }
            backgroundHandlerThread = null
            backgroundHandler = null
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saveImageToDB() {
        if (imageFilePath != null && triesCount > 0) {
            currentApp?.let { appData ->
                val intruder = Intruder(
                    appName = appData.appName,
                    time = System.currentTimeMillis().toString(),
                    imageUrl = imageFilePath!!,
                    tryCount = triesCount
                )
                viewModel.addIntruderToDB(intruder)
            }
        }
    }

    private fun showDialogForgotPassword() {
        binding.textureView.gone()
        val currentLockType = OverlayValidateType.entries.find {
            it.value == preferences.getLockType()
        } ?: OverlayValidateType.TYPE_PATTERN
        val createPasswordType = when (currentLockType) {
            OverlayValidateType.TYPE_PATTERN -> {
                Constants.TYPE_PATTERN
            }

            OverlayValidateType.TYPE_PIN -> {
                Constants.TYPE_PIN_4_digit
            }

            OverlayValidateType.TYPE_KNOCK_CODE -> {
                Constants.TYPE_KNOCK_CODE
            }
        }
        val dialog = DialogForgotPassword.newInstance()
        dialog.onResetPassword = {
            val bundle = bundleOf(
                Constants.KEY_TYPE_PASSWORD to createPasswordType
            )
            val fragment = CreatePasswordFragment.newInstance()
            fragment.onCratePasswordSuccess = {
                binding.lockOverlay.updateIndicator()
                resetTriesCount()
                binding.textureView.visible()
            }
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().add(R.id.fragmentLayout, fragment)
                .addToBackStack("").commit()
        }
        dialog.show(supportFragmentManager, "")
    }

    private fun showDialogQuestionForgotPassword() {
        if (preferences.getSecurityQuestionEnable() && preferences.getSecurityQuestionId() != 0) {
            val dialog = DialogConfirmForgotPassword.newInstance()
            if (!dialog.isShown) {
                dialog.show(supportFragmentManager, "")
            }
            dialog.callback = { ok ->
                if (ok) {
                    showDialogForgotPassword()
                } else {
                    binding.tvUnlockStatus.text = getString(R.string.try_again_after_2_hours)
                }
            }
        } else binding.tvUnlockStatus.text = getString(R.string.try_again_after_2_hours)
    }

    private fun resetTriesCount() {
        binding.tvUnlockStatus.gone()
        preferences.setTimeUnLock(0)
        preferences.setUnlockFirst(false)
        binding.imgFingerPrint.isEnabled = true
        binding.lockOverlayDisable.gone()
        triesCountMax = 5
        triesCountError = 0
    }

    private fun resetUnLockApp() {
        preferences.setTimeUnLock(0)
        binding.lockOverlayDisable.gone()
        triesCountError = 0
        binding.imgFingerPrint.isEnabled = true
    }

    private fun lockUpError() {
        triesCountError++
        if (triesCountError >= triesCountMax) {
            binding.lockOverlayDisable.visible()
            binding.imgFingerPrint.isEnabled = false
            if (triesCountMax == 5) {
                preferences.setUnlockFirst(true)
            }
            preferences.setTimeUnLock(System.currentTimeMillis())
            showDialogQuestionForgotPassword()
        }
    }

    override fun onStart() {
        super.onStart()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    override fun onResume() {
        super.onResume()
        registerGlobalLayoutListener()
    }

    override fun loadAds() {
        cemAdManager.loadBannerAndShowByActivity(
            activity = this,
            viewGroup = binding.layoutBanner.bannerLayout,
            configKey = ConstAd.BANNER_KEY_HOME,
            position = ConstAd.POS_BOTTOM_BANNER,
            nameScreen = this::class.simpleName,
            callback = object : BannerAdListener {
                override fun onBannerLoaded(banner: BannerAdView, view: View) {}

                override fun onBannerFailed(error: String?) {}

                override fun onBannerClicked() {}

                override fun onBannerOpen() {}

                override fun onBannerClose() {}

            }
        )
    }

    override fun onPause() {
        super.onPause()
        if (isActivityShowing && currentApp?.packageName != this.packageName) {
            finishAndRemoveTask()
        }
    }

    override fun onDestroy() {
        currentApp?.let { appData ->
            if (appData.packageName != this@LockActivity.packageName) {
                val bundle = Bundle().apply {
                    putBoolean(KEY_UNLOCK_SUCCESS, isUnLock)
                    putString(KEY_PACKAGE_NAME, appData.packageName)
                }
                ServiceStarter.startServiceWithData(this, bundle)
            }
        }
        triesCountError = 0
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        IS_SHOWING = false
        startActivity(IntentHelper.launcherIntent())
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgFingerPrint -> {
                if (isHaveBiometric()) {
                    biometricPrompt?.authenticate(promptInfo ?: return)
                } else if (isHaveFingerPrint()) {
                    showFingerprintDialog()
                }
            }

            R.id.tvForgotPassword -> {
                if (preferences.getSecurityQuestionEnable() && preferences.getSecurityQuestionId() != 0) {
                    showDialogForgotPassword()
                }
            }
        }
    }

    private val animCallback = AnimatorAdapter { finishActivity() }

    private fun finishActivity() {
        finish()
    }

    private class AnimatorAdapter(val onFinish: () -> Unit) : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            onFinish.invoke()
        }
    }

    companion object {

        const val DURATION = 500L
        const val START_MAIN = "START_MAIN"
        var IS_SHOWING = false

        @JvmStatic
        fun newIntent(context: Context, packageName: String, isStartMain: Boolean = true): Intent {
            IS_SHOWING = true
            val intent = Intent(context, LockActivity::class.java)
            intent.putExtra(KEY_PACKAGE_NAME, packageName)
            intent.putExtra(START_MAIN, isStartMain)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }
    }
}