package com.example.myapplication.ui.custom.customTheme

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.data.model.LockType
import com.example.myapplication.databinding.LayoutPreviewKnockCodeBinding
import com.example.myapplication.databinding.LayoutPreviewLockViewBinding

class PreviewKnockCodeView : ConstraintLayout {

    private lateinit var binding: LayoutPreviewKnockCodeBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = LayoutPreviewKnockCodeBinding.inflate(inflater, this, true)
    }

    fun setKnockColor(color: Int?) {
        color?.let {
            binding.indicator.imageTintList = ColorStateList.valueOf(color)
            binding.knockView.imageTintList = ColorStateList.valueOf(color)
        }
    }
}