package com.suntech.mytools.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suntech.mytools.databinding.DialogSuggestUpdateBinding

class SuggestUpdateDialog : BaseDialogUpdate() {
    private lateinit var binding: DialogSuggestUpdateBinding
    var onClickConfirm: (() -> Unit)? = null
    var onClickCancel: (() -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSuggestUpdateBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        binding.btnConfirm.setOnClickListener { onClickConfirm?.invoke() }

        binding.btnCancel.setOnClickListener { onClickCancel?.invoke() }
    }

}