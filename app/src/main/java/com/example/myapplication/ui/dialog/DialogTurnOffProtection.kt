package com.example.myapplication.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.myapplication.base.BaseDialog
import com.example.myapplication.databinding.DialogConfirmOffProtectionBinding

class DialogTurnOffProtection : BaseDialog<DialogConfirmOffProtectionBinding>() {

    var onClickEvent: (() -> Unit)? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogConfirmOffProtectionBinding {
        return DialogConfirmOffProtectionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTurnOff.setOnClickListener {
            onClickEvent?.invoke()
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = DialogTurnOffProtection().apply {
            arguments = bundleOf()
        }
    }
}