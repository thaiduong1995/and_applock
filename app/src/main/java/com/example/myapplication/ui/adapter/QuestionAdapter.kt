package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.Filter
import com.example.myapplication.data.model.Question
import com.example.myapplication.data.model.SecurityQuestion
import com.example.myapplication.databinding.ItemQuestionBinding

class QuestionAdapter(
    listQuestion: ArrayList<SecurityQuestion> = arrayListOf()
) : BaseAdapter<ItemQuestionBinding, SecurityQuestion>(listQuestion) {

    var onClickEvent: ((SecurityQuestion) -> Unit)? = null

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemQuestionBinding {
        return ItemQuestionBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemQuestionBinding, position: Int) {
        data.getOrNull(position)?.let { question ->
            binding.tvFilterName.text = binding.root.context.getString(question.question)
            if (question.selected) {
                binding.root.setBackgroundResource(R.drawable.bg_question)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_transparent)
            }
            binding.root.setOnClickListener {
                data.onEach { it.selected = false }
                question.selected = true
                onClickEvent?.invoke(question)
            }
        }
    }
}