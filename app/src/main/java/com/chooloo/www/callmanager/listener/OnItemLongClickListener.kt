package com.chooloo.www.callmanager.listener

import androidx.recyclerview.widget.RecyclerView

interface OnItemLongClickListener {
    fun onItemLongClick(holder: RecyclerView.ViewHolder?, data: Any?)
}