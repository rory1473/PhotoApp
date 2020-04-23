package com.example.photoapp.fragments

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class SpacesItemDecoration(space: Int) : RecyclerView.ItemDecoration() {

    var getSpace = space

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView , state: RecyclerView.State ) {
        outRect.left = getSpace
        outRect.right = getSpace
        outRect.bottom = getSpace


        if (parent.getChildLayoutPosition(view) == 0) {
        outRect.top = getSpace
            }
        else {
        outRect.top = 0
    }
    }
}