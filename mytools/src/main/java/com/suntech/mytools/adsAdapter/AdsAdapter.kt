package com.suntech.mytools.adsAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.suntech.mytools.databinding.ItemBannerBinding
import com.suntech.mytools.databinding.ItemNativeBinding
import com.suntech.mytools.mytools.nativeAd.NativeManager
import com.suntech.mytools.tools.gone

abstract class AdsAdapter<T, Y : RecyclerView.ViewHolder>(private var config: AdsAdapterConfig) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<T?>()
    private lateinit var adapter: ConcatAdapter
    private var loadMoreAdapter = LoadMoreAdapter()
    var originalData = mutableListOf<T?>()
        get() {
            return listData.mapNotNull { it }.toMutableList()
        }

    fun get(): ConcatAdapter {
        if (!this::adapter.isInitialized) {
            adapter =
                ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
            if (config.showBanner) {
                adapter.addAdapter(BannerAdapter())
            }
            adapter.addAdapter(this)
            if (config.showNative) {
                adapter.addAdapter(NativeAdapter(""))
            }

            if (config.loadMore) {
                adapter.addAdapter(loadMoreAdapter)
            }
        }

        return adapter
    }

    fun deleteItem(index: Int) {
        val tempList = ArrayList(listData.mapNotNull { it })
        tempList.removeAt(index)
        this.listData.clear()
        this.listData.addAll(getDataWithAdsItem(tempList))
        notifyDataSetChanged()
    }

    fun updateRealItem(index: Int, item: T) {
        val realIndex = getRealIndex(index)
        listData[realIndex] = item
        notifyItemChanged(realIndex)
    }

    fun addItem(item: T) {
        addData(listOf(item))
    }

    fun addData(data: List<T>) {
        removeLoadMore()
        val listSize = listData.size
        val tempList = ArrayList(listData.mapNotNull { it })
        tempList.addAll(data)
        this.listData.clear()
        this.listData.addAll(getDataWithAdsItem(tempList))
        notifyItemRangeInserted(listSize, listData.size - listSize)
    }

    fun clearData() {
        this.listData.clear()
        notifyDataSetChanged()
    }

    fun insertData(data: List<T>) {
        this.listData.clear()
        this.listData.addAll(getDataWithAdsItem(data))
        this.notifyDataSetChanged()
    }

    private fun getRealIndex(index: Int): Int {
        return listData.indexOf(listData.mapNotNull { it }[index])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_CENTER_NATIVE) {
            return NativeViewHolder(
                ItemNativeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else return getViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NativeViewHolder) {
            holder.bindNativeViewHolder()
        } else if (isInstanceOf(holder)) {
            bindItemViewHolder(
                holder as Y,
                position - listData.subList(0, position).filter { it == null }.size
            )
        }
    }

    private inline fun <reified Y> isInstanceOf(y: Y): Boolean = when (Y::class) {
        Y::class -> true
        else -> false
    }

    private fun getDataWithAdsItem(data: List<T>): Collection<T?> {
        val listData = mutableListOf<T?>()
        if (config.showAdsInCenter) {
            data.forEachIndexed { index, appData ->
                if (index > 0 && index % config.itemThresholds == 0) {
                    listData.add(null)
                }
                listData.add(appData)
            }
        } else {
            listData.addAll(data)
        }
        return listData
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position in listData.indices) {
            return if (listData[position] == null) TYPE_CENTER_NATIVE else TYPE_ITEM
        } else return TYPE_ITEM

    }

    fun loadMore() {
        loadMoreAdapter.loadState = LOAD_STATE.LOADING
        loadMoreAdapter.notifyItemChanged(0)
    }

    private fun removeLoadMore() {
        loadMoreAdapter.loadState = LOAD_STATE.SUCCESS
        loadMoreAdapter.notifyItemChanged(0)
    }

    abstract fun getViewHolder(parent: ViewGroup, viewType: Int): Y
    abstract fun bindItemViewHolder(holder: Y, position: Int)

    open fun getLoadMoreViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ProgressBar(parent.context)
        return LoadMoreViewHolder(view)
    }

    open fun bindLoadMore(holder: RecyclerView.ViewHolder, position: Int) {

    }

    companion object {
        const val TYPE_ITEM = 2
        const val TYPE_CENTER_NATIVE = 3
        const val TYPE_BOTTOM_NATIVE = 4
        const val TYPE_HEADER_BANNER = 5
        const val TYPE_LOAD_MORE = 6
    }


    inner class LoadMoreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var loadState = LOAD_STATE.SUCCESS
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return getLoadMoreViewHolder(parent, viewType)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (loadState) {
                LOAD_STATE.LOADING -> holder.itemView.isVisible = true
                LOAD_STATE.ERROR -> holder.itemView.isVisible = false
                LOAD_STATE.SUCCESS -> holder.itemView.isVisible = false
            }
            bindLoadMore(holder, position)
        }

        override fun getItemCount(): Int {
            return 1
        }
    }

    inner class BannerAdapter : RecyclerView.Adapter<BannerAdViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerAdViewHolder {
            return BannerAdViewHolder(
                ItemBannerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun getItemViewType(position: Int): Int {
            return TYPE_HEADER_BANNER
        }

        override fun onBindViewHolder(holder: BannerAdViewHolder, position: Int) {
            holder.onBind()
        }


        override fun getItemCount(): Int {
            return 1
        }
    }


    inner class NativeAdapter(adsKey: String) : RecyclerView.Adapter<NativeViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): NativeViewHolder {
            return NativeViewHolder(
                ItemNativeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: NativeViewHolder, position: Int) {
            holder.bindNativeViewHolder()
        }

        override fun getItemViewType(position: Int): Int {
            return TYPE_BOTTOM_NATIVE
        }

        override fun getItemCount(): Int {
            return 1
        }
    }

    inner class LoadMoreViewHolder(view: View) :
        RecyclerView.ViewHolder(view)
}

enum class LOAD_STATE {
    SUCCESS,
    ERROR,
    LOADING
}