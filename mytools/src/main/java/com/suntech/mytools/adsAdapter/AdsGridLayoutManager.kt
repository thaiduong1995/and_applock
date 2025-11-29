package com.suntech.mytools.adsAdapter

import android.content.Context
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.suntech.mytools.adsAdapter.AdsAdapter

class AdsGridLayoutManager(context: Context, spanCount: Int, concatAdapter: ConcatAdapter) :
    GridLayoutManager(context, spanCount) {
    init {
        spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(concatAdapter.getItemViewType(position)) {
                    AdsAdapter.TYPE_ITEM -> 1
                    else -> spanCount
                }
            }
        }
    }
}