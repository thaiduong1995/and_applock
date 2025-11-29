package  com.example.myapplication.ui.custom.passwordView.pin

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.data.model.CustomTheme
import com.example.myapplication.databinding.LayoutPinViewBinding
import com.example.myapplication.ui.custom.passwordView.InputPasswordListener
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Utils
import java.util.Timer
import java.util.TimerTask

/**
 * Created by Thinhvh on 05/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
class PinLockView : ConstraintLayout {

    private var inputPasswordListener: InputPasswordListener? = null
    private var pinEnteredListener: OnPinEnteredListener? = null
    private var pinLength = PIN_LENGTH
    private var curDigit = 0
    private lateinit var mBinding: LayoutPinViewBinding
    private var keyboardAdapter = KeyboardAdapter()
    private var pinTextView = arrayListOf<IndicatorView>()
    private var showPass = false
    var curPinCode = ""
    var isAnimating = false
    var currentThemeId = Constants.DEFAULT_THEME

    private var keyboard = arrayListOf(
        Keyboard("1", "pin_1.png"),
        Keyboard("2", "pin_2.png"),
        Keyboard("3", "pin_3.png"),
        Keyboard("4", "pin_4.png"),
        Keyboard("5", "pin_5.png"),
        Keyboard("6", "pin_6.png"),
        Keyboard("7", "pin_7.png"),
        Keyboard("8", "pin_8.png"),
        Keyboard("9", "pin_9.png"),
        Keyboard(KEY_DELETE, "pin_delete.png"),
        Keyboard("0", "pin_0.png"),
        Keyboard(KEY_CLEAR, "pin_refresh.png"),
    )

    fun setInputPasswordListener(callback: InputPasswordListener) {
        this.inputPasswordListener = callback
    }

    fun enableRandomKeyboard() {
        keyboard = arrayListOf(
            Keyboard("1", "pin_1.png"),
            Keyboard("2", "pin_2.png"),
            Keyboard("3", "pin_3.png"),
            Keyboard("4", "pin_4.png"),
            Keyboard("5", "pin_5.png"),
            Keyboard("6", "pin_6.png"),
            Keyboard("7", "pin_7.png"),
            Keyboard("8", "pin_8.png"),
            Keyboard("9", "pin_9.png"),
            Keyboard("0", "pin_0.png"),
        )
        keyboard.shuffle()
        keyboard.add(keyboard.size - 1, Keyboard(KEY_DELETE, "pin_delete.png"))
        keyboard.add(Keyboard(KEY_CLEAR, "pin_refresh.png"))
        initKeyboard(context)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding = LayoutPinViewBinding.inflate(inflater, this, true)
        initIndicator(context)
        initKeyboard(context)
    }

    private fun initIndicator(context: Context) {
        pinTextView.clear()
        mBinding.indicator.removeAllViews()
        for (i in 0 until pinLength) {
            val pinImage = IndicatorView(context)
            pinTextView.add(pinImage)
            mBinding.indicator.addView(pinImage)
        }
        pinTextView.forEach {
            it.setThemeId(currentThemeId)
        }
    }

    private fun initKeyboard(context: Context): Unit {
        mBinding.recycler.layoutManager = GridLayoutManager(context, SPAN_COUNT)
        keyboardAdapter.setData(keyboard)
        keyboardAdapter.onClickCallback = {
            inputPasswordListener?.onInputting()
            if (!isAnimating) {
                if (it.name != KEY_CLEAR && it.name != KEY_DELETE) {
                    if (curDigit == 0) {
                        inputPasswordListener?.onStartInput()
                    }
                    if (curDigit != pinLength) {
                        pinTextView[curDigit].setStateSelected(true)
                        curDigit++
                        curPinCode += it.name
                    }
                    if (curDigit == pinLength) {
                        inputPasswordListener?.onInputComplete(curPinCode)
                    }
                } else if (it.name == KEY_DELETE) {
                    if (curDigit != 0) {
                        curPinCode = curPinCode.substring(0, curPinCode.length - 1)
                        pinTextView[curDigit - 1].setStateSelected(false)
                        curDigit--
                    }
                    pinEnteredListener?.onPinDelete()
                } else if (it.name == KEY_CLEAR) {
                    clearPinCode()
                    pinEnteredListener?.onPinEmpty()
                }
            }
        }
        mBinding.recycler.adapter = keyboardAdapter
    }

    fun clearPinCode() {
        curPinCode = ""
        curDigit = 0
        isAnimating = true
        Timer().schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post(Runnable {
                    pinTextView.forEach {
                        it.setStateSelected(false)
                    }
                    isAnimating = false
                })
            }
        }, 300)
    }

    fun setThemeId(themeId: Int) {
        currentThemeId = themeId
        keyboard.forEach { keyboard ->
            keyboard.resPath = Utils.getAssetPath(themeId).plus(keyboard.resPath)
        }
        pinTextView.forEach {
            it.setThemeId(themeId)
        }
    }

    fun changePinLength(pinLength: Int) {
        this.pinLength = pinLength
        mBinding.indicator.removeAllViews()
        initIndicator(context)
        initKeyboard(context)
    }

    fun setOnPinEnteredListener(onPinEnteredListener: OnPinEnteredListener) {
        this.pinEnteredListener = onPinEnteredListener
    }

    fun setCustomTheme(customTheme: CustomTheme) {
        customTheme.numberColor.let {
            keyboardAdapter.setPreviewTypeText(customTheme.numberColor)
        }
    }

    companion object {
        const val PIN_LENGTH = 4
        const val SPAN_COUNT = 3
        const val KEY_CLEAR = "CLEAR"
        const val KEY_DELETE = "DELETE"
        const val DEFAULT_THEME_ID = 0
    }

    interface OnPinEnteredListener {
        fun onPinDelete()
        fun onPinEmpty()
    }
}