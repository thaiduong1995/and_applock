package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.model.Language
import com.example.myapplication.databinding.ItemLanguageBinding

class LanguageAdapter : RecyclerView.Adapter<LanguageAdapter.LanguageVH>() {

    private var languages = arrayListOf<Language>()
    private var onItemClick: ((Language) -> Unit)? = null

    fun setData(listLanguage: List<Language>) {
        languages.clear()
        languages.addAll(listLanguage)
        notifyDataSetChanged()
    }

    fun setItemClickListener(listener: ((Language) -> Unit)?) {
        onItemClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageVH {
        val binding =
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageVH(binding)
    }

    override fun getItemCount(): Int = languages.size

    override fun onBindViewHolder(holder: LanguageVH, position: Int) {
        val item = languages[position]
        holder.bindData(item, position)
        holder.binding.root.setOnClickListener {
            item.isSelected = !item.isSelected
            holder.binding.ivSelectorLanguage.setImageResource(
                if (item.isSelected) R.drawable.ic_checkbox_selected
                else R.drawable.ic_checkbox_normal
            )
            onItemClick?.invoke(item)
        }
    }

    inner class LanguageVH(
        val binding: ItemLanguageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(item: Language, position: Int) {
            binding.tvLanguageName.text = item.name
            binding.ivSelectorLanguage.setImageResource(
                if (item.isSelected) R.drawable.ic_checkbox_selected
                else R.drawable.ic_checkbox_normal
            )
        }
    }
}