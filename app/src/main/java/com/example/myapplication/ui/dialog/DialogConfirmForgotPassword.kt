package com.example.myapplication.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.myapplication.base.BaseDialog
import com.example.myapplication.databinding.DialogConfirmForgotPasswordBinding
import com.example.myapplication.utils.setSingleClick

class DialogConfirmForgotPassword : BaseDialog<DialogConfirmForgotPasswordBinding>() {

    var callback: (Boolean) -> Unit = {}

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): DialogConfirmForgotPasswordBinding {
        return DialogConfirmForgotPasswordBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvConfirm.setSingleClick {
            callback(true)
            dismiss()
        }
        binding.tvCancel.setSingleClick {
            callback(false)
            dismiss()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = DialogConfirmForgotPassword().apply {
            arguments = bundleOf()
        }
    }
}