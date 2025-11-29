package com.example.myapplication.ui.custom.customTheme

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.data.model.LockType
import com.example.myapplication.databinding.LayoutPreviewLockViewBinding

class PreviewLockView : ConstraintLayout {

    private lateinit var binding: LayoutPreviewLockViewBinding
    private var lockType = LockType.PATTERN

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = LayoutPreviewLockViewBinding.inflate(inflater, this, true)
    }

    fun setDotColor(color: Int?) {
        color?.let {
            binding.previewPatternLock.setDotColor(color)
        }
        invalidate()
    }

    fun setBackgroundBitmap(bitmap: Bitmap?) {
        binding.imgBackground.setImageBitmap(bitmap)
        invalidate()
    }

    fun setLineColor(color: Int?) {
        color?.let {
            binding.previewPatternLock.setLineColor(color)
            invalidate()
        }
    }

    fun setKnockColor(color: Int?) {
        color?.let {
            binding.previewKnockCode.setKnockColor(color)
            invalidate()
        }
    }

    fun setNumberColor(numberColor: Int?) {
        numberColor?.let {
            binding.previewPinCode.setNumberColor(numberColor)
        }
    }

    fun setLockType(lockType: LockType) {
        this.lockType = lockType
        when (lockType) {
            LockType.KNOCK -> {
                binding.previewKnockCode.visibility = View.VISIBLE
                binding.previewPatternLock.visibility = View.GONE
            }

            LockType.PATTERN -> {
                binding.previewKnockCode.visibility = View.GONE
                binding.previewPatternLock.visibility = View.VISIBLE
            }

            LockType.PASS_CODE -> {
                binding.previewPinCode.visibility = View.VISIBLE
                binding.previewKnockCode.visibility = View.GONE
                binding.previewPatternLock.visibility = View.GONE
            }
        }
    }
}