package com.cancer.yaqeen.presentation.util.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.cancer.yaqeen.presentation.util.languageIsEnglish

class HorizontalMarginItemDecoration(
    private val spaceHeight: Int
) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val languageIsEnglish: Boolean = languageIsEnglish()
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == 0) {
            if (languageIsEnglish) {
                outRect.right = spaceHeight / 2
                outRect.left = spaceHeight
            } else {
                outRect.right = spaceHeight
                outRect.left = spaceHeight / 2
            }
        } else {
            outRect.left = spaceHeight / 2
            outRect.right = spaceHeight / 2
        }
        outRect.bottom = spaceHeight
    }
}