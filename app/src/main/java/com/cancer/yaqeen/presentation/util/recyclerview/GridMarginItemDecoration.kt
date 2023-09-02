package com.cancer.yaqeen.presentation.util.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridMarginItemDecoration(
    private val spaceHeight: Int
) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = spaceHeight / 2
        outRect.bottom = spaceHeight / 2
        outRect.left = spaceHeight / 2
        outRect.right = spaceHeight / 2
    }
}