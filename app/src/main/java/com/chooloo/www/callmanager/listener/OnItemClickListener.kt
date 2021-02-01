package com.chooloo.www.callmanager.listener

import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener {
    fun onItemClick(holder: RecyclerView.ViewHolder?, data: Any?)
}