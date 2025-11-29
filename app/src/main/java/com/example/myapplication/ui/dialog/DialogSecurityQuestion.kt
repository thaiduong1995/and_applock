package com.example.myapplication.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.model.SecurityQuestion
import com.example.myapplication.databinding.DialogSecurityQuestionBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.extention.getArgumentParcelableArray
import com.example.myapplication.extention.toastMessageShortTime
import com.example.myapplication.ui.adapter.QuestionAdapter
import com.example.myapplication.utils.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DialogSecurityQuestion : BottomSheetDialogFragment() {

    @Inject
    lateinit var preferences: PreferenceHelper

    private var _binding: DialogSecurityQuestionBinding? = null

    private val binding get() = _binding!!
    private var currentQuestion: SecurityQuestion? = null
    private var currentAnswer = ""
    private var createStep = CreateStep.CREATE
    var onConfirm: (Boolean) -> Unit = {}

    override fun getTheme(): Int = R.style.BaseBottomSheetEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSecurityQuestionBinding.inflate(inflater, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        isCancelable = false
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getArgumentParcelableArray<SecurityQuestion>(ITEMS)?.let { list ->
            currentQuestion = list.first { it.selected }
        }

        binding.tvTitle.text = getString(R.string.security_question_create_title)
        binding.tvQuestion.text = currentQuestion?.question?.let { getString(it) }
        binding.tvQuestion.setOnClickListener {
            showDialogSelectQuestion()
        }

        binding.btnConfirm.setOnClickListener {
            val answer = binding.edtInput.text.toString().trim()
            if (answer.isEmpty()) {
                binding.tvError.isVisible = true
                binding.tvInputLength.isVisible = false
                binding.tvError.text = getString(R.string.error_empty_input)
            } else {
                if (createStep == CreateStep.CREATE) {
                    currentAnswer = answer
                    binding.tvTitle.text = getString(R.string.security_question)
                    createStep = CreateStep.CONFIRM
                    binding.btnConfirm.text = getString(R.string.confirm)
                    binding.edtInput.setText("")
                } else {
                    if (answer.lowercase() == currentAnswer) {
                        arguments?.getArgumentParcelableArray<SecurityQuestion>(ITEMS)
                            ?.let { list ->
                                FirebaseEvent.confirmQuestion(getString(list.first { it.selected }.question))
                            }
                        currentQuestion?.question?.let { it1 ->
                            preferences.setSecurityQuestionId(
                                it1
                            )
                        }
                        preferences.setAnswer(answer)
                        context?.toastMessageShortTime(context?.getString(R.string.security_question_created))
                        onConfirm(true)
                        dismiss()
                    } else {
                        binding.tvError.isVisible = true
                        binding.tvInputLength.isVisible = false
                        binding.tvError.text = getString(R.string.wrong_answer)
                    }
                }
            }
        }

        binding.edtInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvError.isVisible = false
                binding.imgClear.isVisible = s?.isNotEmpty() == true
                binding.tvInputLength.isVisible = true
                binding.tvInputLength.text = String.format(
                    binding.root.context.getString(R.string.input_length, s?.length ?: 0, 30)
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.imgClose.setOnClickListener {
            dismiss()
            onConfirm(false)
        }

        binding.imgClear.setOnClickListener {
            binding.edtInput.setText("")
        }
    }

    private fun showDialogSelectQuestion() {
        val popupWindow = PopupWindow(
            LayoutInflater.from(context).inflate(R.layout.layout_popup_select_question, null),
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        val recycleView = popupWindow.contentView.findViewById<RecyclerView>(R.id.recycler)
        recycleView.layoutManager = LinearLayoutManager(context)
        arguments?.getArgumentParcelableArray<SecurityQuestion>(ITEMS)?.let { list ->
            val adapter = QuestionAdapter(ArrayList(list))
            adapter.onClickEvent = {
                binding.tvQuestion.text = getString(it.question)
                currentQuestion = it
                popupWindow.dismiss()
            }
            recycleView.adapter = adapter
        }
        popupWindow.showAsDropDown(binding.tvQuestion)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ITEMS = "ITEMS"

        @JvmStatic
        fun newInstance(
            items: Array<SecurityQuestion> = arrayOf()
        ) = DialogSecurityQuestion().apply {
            arguments = bundleOf(ITEMS to items)
        }
    }
}

enum class CreateStep {
    CREATE, CONFIRM
}