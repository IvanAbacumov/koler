package com.chooloo.www.callmanager.adapter.helper

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperListener {
    /**
     * Called when a view is selected.
     *
     * @param holder The holder of the view to drag.
     */
    fun onItemSelected(holder: RecyclerView.ViewHolder?)

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param holder The holder of the view to drag.
     */
    fun onStartDrag(holder: RecyclerView.ViewHolder?)

    /**
     * Called when a view is requesting a start of a swipe.
     *
     * @param holder The holder of the view to drag.
     */
    fun onStartSwipe(holder: RecyclerView.ViewHolder?)
}