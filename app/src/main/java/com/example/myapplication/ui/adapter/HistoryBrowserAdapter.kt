package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.model.HistoryBrowser
import com.example.myapplication.databinding.ItemDateHistoryBinding
import com.example.myapplication.databinding.ItemHistoryBrowserBinding
import com.example.myapplication.utils.clearTime
import com.example.myapplication.utils.stringToBitMap
import java.text.SimpleDateFormat
import java.util.*

enum class ItemTypeHistory(var code: Int = 0) {
    DATE(1),
    HISTORY(2)
}

class HistoryBrowserAdapter(
    var onClickDelete: (item: HistoryBrowser) -> Unit = {},
    var onClick: (item: HistoryBrowser) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mListHistory: ArrayList<HistoryBrowser> = arrayListOf()

    inner class HistoryViewHolder(val binding: ItemHistoryBrowserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = mListHistory[position]
            binding.ivIcon.setImageBitmap(item.image.stringToBitMap())
            binding.tvTitle.text = item.title
            binding.tvUrl.text = item.url
            binding.ivDelete.setOnClickListener {
                onClickDelete(item)
            }
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    inner class DateViewHolder(val binding: ItemDateHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvDate.text = convertTime(mListHistory[position].time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ItemTypeHistory.DATE.code -> {
                val binding = ItemDateHistoryBinding.inflate(inflater, parent, false)
                DateViewHolder(binding)
            }

            else -> {
                val binding = ItemHistoryBrowserBinding.inflate(inflater, parent, false)
                HistoryViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return mListHistory.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateViewHolder -> holder.bind(position)
            is HistoryViewHolder -> holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mListHistory[position].url.isEmpty()) {
            ItemTypeHistory.DATE.code
        } else {
            ItemTypeHistory.HISTORY.code
        }
    }

    fun setData(listHistory: ArrayList<HistoryBrowser>) {
        mListHistory = listHistory
        notifyDataSetChanged()
    }

    fun convertTime(time: Long): String {
        val calendar = Calendar.getInstance()
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy")
        var textFormat = format.format(date)
        if (calendar.clearTime().timeInMillis == time) {
            textFormat = "Today - $textFormat"
        }
        return textFormat
    }
}