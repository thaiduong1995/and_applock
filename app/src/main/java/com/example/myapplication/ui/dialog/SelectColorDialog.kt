package com.example.myapplication.ui.dialog

import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseDialog
import com.example.myapplication.databinding.DialogSelectColorBinding
import com.example.myapplication.ui.adapter.ColorAdapter

class SelectColorDialog : BaseDialog<DialogSelectColorBinding>() {

    var onClickColor: ((Int) -> Unit)? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSelectColorBinding {
        return DialogSelectColorBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.doOnPreDraw {
            initRecycler()
        }
    }

    private fun initRecycler() {
        val adapter = ColorAdapter(getListColor())
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.recycler.adapter = adapter
        adapter.onClickEvent = {
            onClickColor?.invoke(it)
            dismiss()
        }
    }

    private fun getListColor(): ArrayList<Int> {
        val ta: TypedArray = requireContext().resources.obtainTypedArray(R.array.color)
        val colors = IntArray(ta.length())
        for (i in 0 until ta.length()) {
            colors[i] = Color.parseColor(ta.getString(i))
        }
        ta.recycle()
        return ArrayList(colors.toList())
    }

    companion object {

        @JvmStatic
        fun newInstance() = SelectColorDialog().apply {
            arguments = bundleOf()
        }
    }
}