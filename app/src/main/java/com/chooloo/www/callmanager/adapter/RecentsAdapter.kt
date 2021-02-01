package com.chooloo.www.callmanager.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chooloo.www.callmanager.R
import com.chooloo.www.callmanager.database.entity.RecentCall
import com.chooloo.www.callmanager.listener.OnItemClickListener
import com.chooloo.www.callmanager.listener.OnItemLongClickListener
import com.chooloo.www.callmanager.ui.ListItemHolder
import com.chooloo.www.callmanager.util.RelativeTime
import java.util.*

class RecentsAdapter
/**
 * Constructor
 *
 * @param context
 * @param cursor
 * @param onItemClickListener
 * @param onItemLongClickListener
 */(context: Context?,
    cursor: Cursor?,
        // Click listeners
    private val mOnItemClickListener: OnItemClickListener?,
    private val mOnItemLongClickListener: OnItemLongClickListener?) : AbsFastScrollerAdapter<ListItemHolder>(context, cursor) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
        return ListItemHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ListItemHolder, cursor: Cursor?) {

        // get the recent call
        val recentCall = RecentCall(mContext, cursor)

        // get information
        var callerName = recentCall.callerName
        val phoneNumber = recentCall.callerNumber
        val date = recentCall.callDate
        if (callerName == null) callerName = phoneNumber
        //            callerName = PhoneNumberUtils.formatPhoneNumber(mContext, phoneNumber);

        // hide header
        holder.header.visibility = View.GONE

        // append calls in a row count
        if (recentCall.count > 1) callerName += " (" + recentCall.count + ")"

        // set date
        holder.bigText.text = callerName ?: phoneNumber
        holder.smallText.text = RelativeTime.getTimeAgo(date.time)

        // set image
        holder.photo.visibility = View.VISIBLE
        holder.photoPlaceholder.visibility = View.GONE
        val callTypeImage: MutableMap<Int, Int> = HashMap()
        callTypeImage[RecentCall.TYPE_INCOMING] = R.drawable.ic_call_received_black_24dp
        callTypeImage[RecentCall.TYPE_OUTGOING] = R.drawable.ic_call_made_black_24dp
        callTypeImage[RecentCall.TYPE_MISSED] = R.drawable.ic_call_missed_black_24dp
        callTypeImage[RecentCall.TYPE_REJECTED] = R.drawable.ic_call_missed_outgoing_black_24dp
        callTypeImage[RecentCall.TYPE_VOICEMAIL] = R.drawable.ic_voicemail_black_24dp
        try {
            holder.photo.setImageResource(callTypeImage[recentCall.callType]!!)
        } catch (e: Exception) {
            holder.photo.visibility = View.GONE
        }


        // set click listeners
        if (mOnItemClickListener != null) holder.itemView.setOnClickListener { v: View? -> mOnItemClickListener.onItemClick(holder, recentCall) }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener { v: View? ->
                mOnItemLongClickListener.onItemLongClick(holder, recentCall)
                true
            }
        }
    }

    override fun getHeaderString(position: Int): String? {
        return null
    }

    override fun refreshHeaders() {}
}