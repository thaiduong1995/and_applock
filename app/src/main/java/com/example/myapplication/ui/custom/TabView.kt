package com.example.myapplication.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.databinding.TabViewBinding

class TabView : ConstraintLayout {

    private var binding: TabViewBinding =
        TabViewBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setImageResource(id: Int) {
        binding.imgIcon.setImageResource(id)
    }

    fun setTitleResource(@StringRes id: Int) {
        binding.tvTitle.text = context.getString(id)
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        binding.imgIcon.isSelected = selected
    }
}