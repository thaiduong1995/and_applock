package com.example.myapplication.ui.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogForgotPasswordBinding
import com.example.myapplication.extention.disable
import com.example.myapplication.extention.enable
import com.example.myapplication.utils.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DialogForgotPassword : BottomSheetDialogFragment() {

    var onResetPassword: () -> Unit = {}

    @Inject
    lateinit var preferences: PreferenceHelper

    private var _binding: DialogForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BaseBottomSheetEditText)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initListener()
    }

    private fun initUI() {
        binding.btnConfirm.disable()
        binding.tvQuestion.text = requireContext().getString(preferences.getSecurityQuestionId())
    }

    private fun initListener() {
        binding.edtInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvError.isVisible = false
                binding.imgClear.isVisible = s?.isNotEmpty() == true
                binding.tvInputLength.isVisible = true
                binding.tvInputLength.text = getString(R.string.input_length, s?.length ?: 0, 30)
                binding.btnConfirm.enable()
                binding.btnConfirm.text = getString(R.string.confirm)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.imgClose.setOnClickListener {
            dismiss()
        }

        binding.imgClear.setOnClickListener {
            binding.edtInput.setText("")
        }

        binding.btnConfirm.setOnClickListener {
            if (binding.edtInput.text.toString().trim() == preferences.getAnswer()) {
                onResetPassword()
                dismiss()
            } else {
                binding.tvError.isVisible = true
                binding.tvError.text = getString(R.string.fail_to_unlock)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() = DialogForgotPassword().apply {
            arguments = bundleOf()
        }
    }
}