package com.example.myapplication.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.myapplication.base.BaseDialog
import com.example.myapplication.data.model.Language
import com.example.myapplication.databinding.DialogLanguageOptionBinding
import com.example.myapplication.extention.getArgumentParcelableArray
import com.example.myapplication.ui.adapter.LanguageAdapter

class DialogLanguageOption : BaseDialog<DialogLanguageOptionBinding>() {

    private var adapterLanguage = LanguageAdapter()
    var onClickEvent: ((language: Language) -> Unit)? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogLanguageOptionBinding {
        return DialogLanguageOptionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getArgumentParcelableArray<Language>(LANGUAGES)
            ?.let { adapterLanguage.setData(it) }
        adapterLanguage.setItemClickListener { item ->
            onClickEvent?.invoke(item)
        }
        binding.rcLanguage.adapter = adapterLanguage
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {

        private const val LANGUAGES = "LANGUAGES"

        @JvmStatic
        fun newInstance(
            languages: Array<Language> = arrayOf()
        ) = DialogLanguageOption().apply {
            arguments = bundleOf(LANGUAGES to languages)
        }
    }
}