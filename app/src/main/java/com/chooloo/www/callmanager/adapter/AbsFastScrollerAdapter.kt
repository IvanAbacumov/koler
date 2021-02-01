package com.chooloo.www.callmanager.adapter

import android.content.Context
import android.database.Cursor
import androidx.recyclerview.widget.RecyclerView

abstract class AbsFastScrollerAdapter<VH : RecyclerView.ViewHolder?>(context: Context?, cursor: Cursor?) : AbsCursorRecyclerViewAdapter<VH>(context!!, cursor) {
    abstract fun getHeaderString(position: Int): String?
    abstract fun refreshHeaders()
}