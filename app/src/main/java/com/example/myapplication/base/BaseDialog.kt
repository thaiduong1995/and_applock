package com.example.myapplication.base

import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.example.myapplication.R
import kotlin.math.roundToInt

abstract class BaseDialog<T : ViewBinding> : DialogFragment() {

    lateinit var binding: T

    override fun onStart() {
        super.onStart()
        isCancelable = false
    }

    override fun getTheme(): Int {
        return R.style.RoundedCornersDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (!this::binding.isInitialized) {
            binding = createView(inflater, container)
        }
        return binding.root
    }

    abstract fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): T

    fun setSize(widthPercentage: Int, heightPercentage: Int) {
        val newWidth = widthPercentage.div(100f)
        val newHeight = heightPercentage.div(100f)
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * newWidth
        val percentHeight = rect.height() * newHeight
        dialog?.window?.setLayout(percentWidth.roundToInt(), percentHeight.roundToInt())
    }

    var isShown = false
        private set

    override fun show(manager: FragmentManager, tag: String?) {
        if (isShown) return
        super.show(manager, tag)
        isShown = true
    }

    override fun onDismiss(dialog: DialogInterface) {
        isShown = false
        super.onDismiss(dialog)
    }
}