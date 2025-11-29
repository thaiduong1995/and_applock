package com.example.myapplication.ui.dialog.fingerprint

import android.app.Activity
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Created by Thinhvh on 15/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
class CustomBottomSheetDialog(val context: Activity?) {

    private val STATE_SET_ON = intArrayOf(R.attr.state_on, -R.attr.state_off, -R.attr.state_error)
    private val STATE_SET_OFF = intArrayOf(-R.attr.state_on, R.attr.state_off, -R.attr.state_error)
    private val STATE_SET_ERROR =
        intArrayOf(-R.attr.state_on, -R.attr.state_off, R.attr.state_error)

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var listern: CustomBottomSheetListern? = null
    private var msgError: String = ""
    private var msgInstruction: String = ""
    private var textview_tittle: AppCompatTextView? = null
    private var textview_subtittle: AppCompatTextView? = null
    private var textview_description: AppCompatTextView? = null
    private var textview_finger_touch: AppCompatTextView? = null
    private var imageview_icon: AppCompatImageView? = null
    private var imageview_finger: AppCompatImageView? = null
    private var button_cancel_finger: AppCompatButton? = null

    private fun initDialog() {
        val view = context?.layoutInflater?.inflate(R.layout.bottom_sheet_finger_print, null)
        view?.let {
            bottomSheetDialog?.setContentView(view)
            textview_finger_touch =
                bottomSheetDialog?.findViewById(R.id.textview_finger_touch)
            imageview_finger = bottomSheetDialog?.findViewById(R.id.imageview_finger)
            button_cancel_finger =
                bottomSheetDialog?.findViewById(R.id.button_cancel_finger)
            bottomSheetDialog?.findViewById<Button>(R.id.button_cancel_finger)?.setOnClickListener {
                close()
            }
            bottomSheetDialog?.setOnDismissListener {
                listern?.closed()
            }
        }
    }

    fun setListern(listern: CustomBottomSheetListern): CustomBottomSheetDialog {
        this.listern = listern
        return this
    }

    fun setTittle(tittle: String): CustomBottomSheetDialog {
        textview_tittle?.text = tittle
        return this
    }

    fun setSubTittle(subTittle: String): CustomBottomSheetDialog {
        textview_subtittle?.text = subTittle
        return this
    }

    fun setDescription(description: String): CustomBottomSheetDialog {
        textview_description?.text = description
        return this
    }

    fun seFingerprintInstruction(instruction: String): CustomBottomSheetDialog {
        msgInstruction = instruction
        textview_finger_touch?.text = instruction
        return this
    }

    fun setMsgError(error: String): CustomBottomSheetDialog {
        msgError = error
        return this
    }

    fun setIcon(icon: Drawable?): CustomBottomSheetDialog {
        imageview_icon?.setImageDrawable(icon)
        if (icon != null) {
            imageview_icon?.visibility = View.VISIBLE
        } else {
            imageview_icon?.visibility = View.GONE
        }
        return this
    }

    fun setColorPrimary(@ColorRes color: Int) {
        try {
            if (context != null) {
                button_cancel_finger?.setTextColor(ContextCompat.getColor(context, color))
                val gd = imageview_finger?.background as GradientDrawable
                gd.setColor(ContextCompat.getColor(context, color))
            }
        } catch (e: Exception) {

        }
    }

    fun showError() {
        if (msgError.isEmpty()) {
            textview_finger_touch?.text = context?.getString(R.string.text_erro_not_reconized)
        } else {
            textview_finger_touch?.text = msgError
        }
        if (context != null)
            textview_finger_touch?.setTextColor(ContextCompat.getColor(context, R.color.redF03738))
        imageview_finger?.setImageState(STATE_SET_ERROR, true)
        startTimer()
    }

    private fun startTimer() {
        val handle = Handler()
        handle.postDelayed({ showIntructionDefault() }, 1500)
    }

    private fun showIntructionDefault() {
        if (msgInstruction.isEmpty()) {
            textview_finger_touch?.text = context?.getString(R.string.text_default_instruction)
        } else {
            textview_finger_touch?.text = msgInstruction
        }
        if (context != null)
            textview_finger_touch?.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    fun close() {
        if (bottomSheetDialog?.isShowing == true) {
            try {
                bottomSheetDialog?.dismiss()
                listern?.closed()
            } catch (e: Exception) {

            }
        }
    }

    fun show() {
        try {
            bottomSheetDialog?.show()
            listern?.open()
        } catch (e: Exception) {

        }
    }

    fun isShowing(): Boolean {
        return bottomSheetDialog?.isShowing ?: false
    }

    init {
        if (context != null) {
            bottomSheetDialog = BottomSheetDialog(context)
            initDialog()
        }
    }

    interface CustomBottomSheetListern {
        fun open()
        fun closed()
    }
}