package com.chooloo.www.callmanager.adapter.helper

import android.graphics.Canvas
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chooloo.www.callmanager.util.Utilities

/**
 * A helper influenced by [https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf](Medium's Article)
 * and also [https://github.com/brianwernick/RecyclerExt/blob/master/library/src/main/java/com/devbrackets/android/recyclerext/adapter/helper/SimpleElevationItemTouchHelperCallback.java](SimpleElevationItemTouchHelperCallback)
 */
class SimpleItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    protected var isElevated = false
    protected var originalElevation = 0f
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false //For the time being
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?,
                                   actionState: Int) {
        // We only want the active item
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is ItemTouchHelperViewHolder) {
                val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
                itemViewHolder.onItemSelected()
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView,
                           viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is ItemTouchHelperViewHolder) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemClear()
        }
        updateElevation(recyclerView, viewHolder, false)
    }

    override fun onChildDraw(c: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {

        //To avoid elevation conflicts with the Lollipop+ implementation, we will always inform the super that we aren't active
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false)
        if (isCurrentlyActive && !isElevated) {
            updateElevation(recyclerView, viewHolder, true)
        }
    }

    /**
     * Updates the elevation for the specified `holder` by either increasing
     * or decreasing by the specified amount
     *
     * @param recyclerView The recyclerView to use when calculating the new elevation
     * @param holder       The ViewHolder to increase or decrease the elevation for
     * @param elevate      True if the `holder` should have it's elevation increased
     */
    protected fun updateElevation(recyclerView: RecyclerView, holder: RecyclerView.ViewHolder, elevate: Boolean) {
        if (elevate) {
            originalElevation = ViewCompat.getElevation(holder.itemView)
            val newElevation = Utilities.convertDpToPixel(recyclerView.context, 4f)
            ViewCompat.setElevation(holder.itemView, newElevation)
            isElevated = true
        } else {
            ViewCompat.setElevation(holder.itemView, originalElevation)
            originalElevation = 0f
            isElevated = false
        }
    }

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun onItemDismiss(position: Int)
    }
}