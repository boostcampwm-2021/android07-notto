package com.gojol.notto.ui.home.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.gojol.notto.R

class GridSpacingDecoration(
    private val deviceWidth: Int,
    private val spanCount: Int
) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // (디바이스 가로크기 - 7 * 아이템 가로크기) / 7
        val itemWidth = parent.context.resources.getDimension(R.dimen.calendar_day_size)
        val spacing = ((deviceWidth - 7 * itemWidth) / 7).toInt()
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = column * spacing / spanCount
        outRect.right = spacing - (column + 1) * spacing / spanCount
    }
}
