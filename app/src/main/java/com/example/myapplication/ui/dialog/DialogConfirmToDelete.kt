package com.example.myapplication.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.myapplication.base.BaseDialog
import com.example.myapplication.databinding.DialogConfirmDeleteBinding

class DialogConfirmToDelete : BaseDialog<DialogConfirmDeleteBinding>() {

    var callback: () -> Unit = {}

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogConfirmDeleteBinding {
        return DialogConfirmDeleteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(TITLE)?.let {
            binding.tvTitle.text = it
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvConfirm.setOnClickListener {
            callback.invoke()
            dismiss()
        }
    }

    companion object {

        private const val TITLE = "TITLE"

        @JvmStatic
        fun newInstance(title: String? = null) = DialogConfirmToDelete().apply {
            arguments = bundleOf(TITLE to title)
        }
    }
}