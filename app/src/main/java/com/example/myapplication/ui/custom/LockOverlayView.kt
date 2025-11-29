package com.example.myapplication.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.R
import com.example.myapplication.data.model.CustomTheme
import com.example.myapplication.data.model.IndicatorType
import com.example.myapplication.data.model.LockType
import com.example.myapplication.data.model.OverlayValidateType
import com.example.myapplication.databinding.LayoutLockOverlayViewBinding
import com.example.myapplication.extention.getKnockCodeLength
import com.example.myapplication.ui.custom.passwordView.InputPasswordListener
import com.example.myapplication.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LockOverlayView : ConstraintLayout {
    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    private lateinit var mBinding: LayoutLockOverlayViewBinding
    private var lockType = OverlayValidateType.TYPE_PATTERN
    private var isSuccess: Boolean = false

    constructor(context: Context) : super(context)

    private var callback: OverlayCallback? = null

    fun setCallback(callback: OverlayCallback) {
        this.callback = callback
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding = LayoutLockOverlayViewBinding.inflate(inflater, this, true)
        initViewWithType()
        initListener()
    }

    private fun initListener() {
        if (preferenceHelper.getRandomKeyboard()) {
            mBinding.pinLockView.enableRandomKeyboard()
        }
        mBinding.pinLockView.changePinLength(preferenceHelper.getPinCode().length)
        mBinding.pinLockView.setInputPasswordListener(object : InputPasswordListener {
            override fun onStartInput() {
                callback?.onStartInput()
            }

            override fun onInputComplete(password: String) {
                if (password == preferenceHelper.getPinCode()) {
                    if (!isSuccess) {
                        isSuccess = true
                        callback?.onUnlockSuccess()
                    }
                } else {
                    callback?.onUnLockError(context.getString(R.string.pin_incorrect))
                    mBinding.pinLockView.clearPinCode()
                }
            }

            override fun onInputting() {
                callback?.onInputting()
            }
        })

        mBinding.patternLock.setSecureMode(preferenceHelper.isHidePatternTrails())
        mBinding.patternLock.setInputPasswordListener(object : InputPasswordListener {
            override fun onStartInput() {
                callback?.onStartInput()
            }

            override fun onInputComplete(password: String) {
                if (password == preferenceHelper.getPatternCode() && !isSuccess) {
                    isSuccess = true
                    callback?.onUnlockSuccess()
                } else {
                    callback?.onUnLockError(context.getString(R.string.pattern_incorrect))
                    mBinding.patternLock.onError()
                }
            }

            override fun onInputting() {
                callback?.onInputting()
            }
        })

//        mBinding.knockIndicator.setHeightWidth(Utils.dp2px(context, 50f), Utils.dp2px(context, 50f))
        mBinding.knockView.setIndicator(mBinding.knockIndicator)
        if (preferenceHelper.getKnockCode().isNotEmpty()) {
            mBinding.knockView.setPasswordLength(
                preferenceHelper.getKnockCode().getKnockCodeLength()
            )
        }
        mBinding.knockView.setInputPasswordListener(object : InputPasswordListener {
            override fun onStartInput() {
                callback?.onStartInput()
            }

            override fun onInputComplete(password: String) {
                if (password == preferenceHelper.getKnockCode() && !isSuccess) {
                    isSuccess = true
                    callback?.onUnlockSuccess()
                } else {
                    mBinding.knockView.clearClicks()
                    callback?.onUnLockError(context.getString(R.string.knock_incorrect))
                }
            }

            override fun onInputting() {
                callback?.onInputting()
            }
        })
    }

    private fun initViewWithType() {
        when (lockType.value) {
            OverlayValidateType.TYPE_PATTERN.value -> {
                mBinding.patternLock.visibility = VISIBLE
                mBinding.layoutKnockCode.visibility = GONE
                mBinding.layoutPinCode.visibility = GONE
            }

            OverlayValidateType.TYPE_KNOCK_CODE.value -> {
                mBinding.patternLock.visibility = GONE
                mBinding.layoutKnockCode.visibility = VISIBLE
                mBinding.layoutPinCode.visibility = GONE
            }

            OverlayValidateType.TYPE_PIN.value -> {
                mBinding.patternLock.visibility = GONE
                mBinding.layoutKnockCode.visibility = GONE
                mBinding.layoutPinCode.visibility = VISIBLE
            }
        }
    }

    fun setTheme(themeId: Int) {
        mBinding.pinLockView.setThemeId(themeId)
        mBinding.patternLock.setThemeId(themeId)
        mBinding.knockView.setThemeId(themeId)
    }

    fun setCustomTheme(customTheme: CustomTheme) {
        when (customTheme.lockType) {
            LockType.PATTERN.id -> {
                mBinding.patternLock.setCustomTheme(customTheme)
            }

            LockType.KNOCK.id -> {
                mBinding.knockView.setCustomTheme(customTheme)
            }

            LockType.PASS_CODE.id -> {
                mBinding.pinLockView.setCustomTheme(customTheme)
            }
        }
    }

    fun updateIndicator() {
        mBinding.pinLockView.changePinLength(preferenceHelper.getPinCode().length)
    }

    fun setIndicatorType(type: IndicatorType) {
        mBinding.knockIndicator.setIndicatorType(type)
    }

    init {
        this.lockType =
            OverlayValidateType.values().find { it.value == preferenceHelper.getLockType() }
                ?: OverlayValidateType.TYPE_PATTERN
        initViewWithType()
    }

    interface OverlayCallback {
        fun onStartInput()
        fun onUnlockSuccess()
        fun onUnLockError(msg: String)
        fun onInputting()
    }
}