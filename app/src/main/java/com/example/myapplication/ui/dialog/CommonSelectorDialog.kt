package com.example.myapplication.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.base.BaseDialog
import com.example.myapplication.data.model.CommonSelector
import com.example.myapplication.databinding.DialogCommonSelectorBinding
import com.example.myapplication.ui.adapter.CommonSelectorAdapter

/**
 * Created by Thinhvh on 30/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
class CommonSelectorDialog : BaseDialog<DialogCommonSelectorBinding>() {

    private var adapter = CommonSelectorAdapter()
    private var listCommonSelector = listOf<CommonSelector>()
    var onClickEvent: ((CommonSelector) -> Unit?)? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogCommonSelectorBinding {
        return DialogCommonSelectorBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvCancel.setOnClickListener {
                dismiss()
            }
            recycler.layoutManager = LinearLayoutManager(context)
            adapter.onClickCallback = {
                onClickEvent?.invoke(it)
                dismiss()
            }
            adapter.setData(listCommonSelector)
            recycler.adapter = adapter

            binding.tvTitle.text = arguments?.getString(TITLE)
            binding.tvDec.text = arguments?.getString(DES)
            binding.tvDec.isVisible = arguments?.getString(DES) != null
        }
    }

    fun setData(data: List<CommonSelector>) {
        this.listCommonSelector = data
    }

    companion object {

        private const val TITLE = "TITLE"
        private const val DES = "DES"

        @JvmStatic
        fun newInstance(title: String? = null, dec: String? = null) = CommonSelectorDialog().apply {
            arguments = bundleOf(TITLE to title, DES to dec)
        }
    }
}