package com.example.myapplication.ui.custom.customTheme

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.LayoutPreviewPinCodeBinding

class PreviewPinCodeView : ConstraintLayout {

    private val adapter = NumberAdapter(arrayListOf())
    private lateinit var binding: LayoutPreviewPinCodeBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = LayoutPreviewPinCodeBinding.inflate(inflater, this, true)
        initUI()
    }

    private fun initUI() {
        val data = arrayListOf(
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "delete",
            "0",
            "refresh"
        )
        adapter.setData(data)
        binding.recycler.layoutManager = GridLayoutManager(context, 3)
        binding.recycler.adapter = adapter

    }

    fun setNumberColor(numberColor: Int?) {
        numberColor?.let {
            adapter.setNumberColor(it)
        }
    }
}