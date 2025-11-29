package com.example.myapplication.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.myapplication.R
import com.example.myapplication.base.BaseDialog
import com.example.myapplication.databinding.DialogConfirmToUnlockBinding

class DialogConfirmToUnlock : BaseDialog<DialogConfirmToUnlockBinding>() {

    var callback: () -> Unit = {}
    var onCancel: () -> Unit = {}

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): DialogConfirmToUnlockBinding {
        return DialogConfirmToUnlockBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvMessage.text =
            if (arguments?.getBoolean(IS_CHECK) == false) getString(R.string.confirm_to_unlock) else getString(
                R.string.confirm_to_lock
            )

        binding.tvConfirm.text =
            if (arguments?.getBoolean(IS_CHECK) == false) getString(R.string.unlock) else getString(
                R.string.lock
            )

        binding.tvCancel.setOnClickListener {
            onCancel.invoke()
            dismiss()
        }

        binding.tvConfirm.setOnClickListener {
            callback.invoke()
            dismiss()
        }
    }

    companion object {

        private const val IS_CHECK = "IS_CHECK"

        @JvmStatic
        fun newInstance(isCheck: Boolean = false) = DialogConfirmToUnlock().apply {
            arguments = bundleOf(IS_CHECK to isCheck)
        }
    }
}