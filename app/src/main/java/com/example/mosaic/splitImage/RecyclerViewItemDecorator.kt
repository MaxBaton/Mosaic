package com.example.mosaic.splitImage

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewItemDecorator(private val spaceInPixel: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = spaceInPixel
        outRect.right = 0
        outRect.bottom = spaceInPixel
        outRect.top = 0
    }
}