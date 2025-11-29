package com.example.myapplication.ui.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.model.Question
import com.example.myapplication.databinding.ItemFaqBinding
import com.example.myapplication.extention.gone
import com.example.myapplication.extention.visible

class FAQAdapter : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    private var listQuestions = arrayListOf<Question>()

    fun setList(list: ArrayList<Question>) {
        listQuestions.clear()
        listQuestions.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FAQViewHolder(binding)
    }

    override fun getItemCount() = listQuestions.size

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val item = listQuestions[position]
        holder.bindData(item)
    }

    class FAQViewHolder(val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Question) {
            binding.ivImageFAQ.setImageResource(item.icon)
            binding.tvQuestion.text = binding.root.context.getString(item.question)
            binding.expandableLayout.text =
                Html.fromHtml(binding.root.context.getString(item.description))
            binding.ivArrowDown.setOnClickListener {
                if (item.isExpanded) {
                    item.isExpanded = false
                    binding.expandableLayout.gone()
                    binding.ivArrowDown.setImageResource(R.drawable.ic_chevron_down)
                } else {
                    item.isExpanded = true
                    binding.expandableLayout.visible()
                    binding.ivArrowDown.setImageResource(R.drawable.ic_chevron_up)
                }
            }
        }
    }
}