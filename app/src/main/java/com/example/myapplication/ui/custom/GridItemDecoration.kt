package com.example.myapplication.ui.custom

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.extention.dp2px

/**
 * Created by Thinhvh on 04/11/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
class GridItemDecoration(
    context: Context, var spanCount: Int
) : RecyclerView.ItemDecoration() {

    private var padding = 0

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position: Int = parent.getChildAdapterPosition(view)
        val column: Int = position % spanCount
        outRect.left = column * padding / spanCount
        outRect.right =
            padding - (column + 1) * padding / spanCount
        if (position >= spanCount) {
            outRect.top = padding
        }
    }

    init {
        padding = context.dp2px(12f)
    }
}