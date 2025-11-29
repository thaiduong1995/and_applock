package com.example.myapplication.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.myapplication.base.BaseDialog
import com.example.myapplication.databinding.DialogConfirmGetLockAppBinding
import com.example.myapplication.utils.setSingleClick

class DialogConfirmLockMoreFun : BaseDialog<DialogConfirmGetLockAppBinding>() {

    var callbackConfirm: () -> Unit = {}
    var callbackCancel: () -> Unit = {}

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): DialogConfirmGetLockAppBinding {
        return DialogConfirmGetLockAppBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(TITLE)?.let {
            binding.tvMessage.text = it
        }
        arguments?.getInt(RESID)?.let {
            binding.imgIcon.setImageResource(it)
        }
        binding.tvConfirm.setSingleClick {
            callbackConfirm()
            dismiss()
        }
        binding.tvCancel.setSingleClick {
            callbackCancel()
            dismiss()
        }
    }

    companion object {

        private const val TITLE = "TITLE"
        private const val RESID = "resId"

        @JvmStatic
        fun newInstance(title: String? = null, resId: Int? = null) =
            DialogConfirmLockMoreFun().apply {
                arguments = bundleOf(TITLE to title, RESID to resId)
            }
    }
}