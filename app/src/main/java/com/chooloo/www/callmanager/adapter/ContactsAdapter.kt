package com.chooloo.www.callmanager.adapter

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chooloo.www.callmanager.R
import com.chooloo.www.callmanager.cursorloader.FavoritesAndContactsLoader
import com.chooloo.www.callmanager.database.entity.Contact
import com.chooloo.www.callmanager.listener.OnItemClickListener
import com.chooloo.www.callmanager.listener.OnItemLongClickListener
import com.chooloo.www.callmanager.ui.ListItemHolder
import com.chooloo.www.callmanager.util.PhoneNumberUtils
import timber.log.Timber

class ContactsAdapter
/**
 * Constructor
 *
 * @param context
 * @param cursor
 */(context: Context?,
    cursor: Cursor?,
        // Click listeners
    private val mOnItemClickListener: OnItemClickListener?,
    private val mOnItemLongClickListener: OnItemLongClickListener?) : AbsFastScrollerAdapter<ListItemHolder>(context, cursor) {
    private var mOnContactSelectedListener: OnContactSelectedListener? = null
    private val holderMap = ArrayMap<ListItemHolder, Int>()

    // List of contact sublist mHeaders
    private var mHeaders: Array<String?>? = arrayOfNulls(0)

    // Number of contacts that correspond to each mHeader in {@code mHeaders}.
    private var mCounts: IntArray? = IntArray(0)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false)
        return ListItemHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ListItemHolder, cursor: Cursor?) {

        // get the contact from the cursor
        val contact = Contact(cursor)

        // some settings
        val position = cursor!!.position
        val header = getHeaderString(position)
        holderMap[viewHolder] = position

        // set texts
        viewHolder.bigText.text = contact.name
        viewHolder.smallText.text = PhoneNumberUtils.formatPhoneNumber(mContext, contact.mainPhoneNumber)

        // set header
        val showHeader = position == 0 || header != getHeaderString(position - 1)
        viewHolder.header.text = header
        viewHolder.header.visibility = if (showHeader) View.VISIBLE else View.INVISIBLE

        // set photo
        if (contact.photoUri == null) {
            viewHolder.photo.visibility = View.GONE
            viewHolder.photoPlaceholder.visibility = View.VISIBLE
        } else {
            viewHolder.photo.visibility = View.VISIBLE
            viewHolder.photoPlaceholder.visibility = View.GONE
            viewHolder.photo.setImageURI(Uri.parse(contact.photoUri))
        }

        // Set click listeners
        if (mOnContactSelectedListener != null) viewHolder.itemView.setOnClickListener { v: View? -> mOnContactSelectedListener!!.onContactSelected(contact.mainPhoneNumber) }
        if (mOnItemClickListener != null) viewHolder.itemView.setOnClickListener { v: View? -> mOnItemClickListener.onItemClick(viewHolder, contact) }
        if (mOnItemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener { v: View? ->
                mOnItemLongClickListener.onItemLongClick(viewHolder, contact)
                true
            }
        }
    }

    override fun changeCursor(cursor: Cursor) {
        super.changeCursor(cursor)
        val tempHeaders = cursor.extras.getStringArray(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX_TITLES)
        val tempCounts = cursor.extras.getIntArray(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX_COUNTS)
        val favoritesCount = cursor.extras.getInt(FavoritesAndContactsLoader.FAVORITES_COUNT)
        if (favoritesCount == 0) {
            mHeaders = tempHeaders
            mCounts = tempCounts
        } else {
            mHeaders = arrayOfNulls((tempHeaders?.size ?: 0) + 1)
            mHeaders!![0] = "★"
            System.arraycopy(tempHeaders!!, 0, mHeaders, 1, mHeaders!!.size - 1)
            mCounts = IntArray(tempCounts!!.size + 1)
            mCounts!![0] = favoritesCount
            System.arraycopy(tempCounts, 0, mCounts, 1, mCounts!!.size - 1)
            if (mCounts != null) {
                var sum = 0
                for (count in mCounts!!) sum += count
                if (sum != cursor.count) Timber.e("Count sum (%d) != mCursor count (%d).", sum, cursor.count)
            }
        }
    }

    override fun getHeaderString(position: Int): String? {
        var index = -1
        var sum = 0
        while (sum <= position) {
            if (index + 1 >= mCounts!!.size) return "?" // index is bigger than headers list size
            sum += mCounts!![++index]
        }
        return mHeaders!![index]
    }

    override fun refreshHeaders() {
        for (holder in holderMap.keys) {
            val position = holderMap[holder]!!
            val showHeader = position == 0 || getHeaderString(position) != getHeaderString(position - 1)
            val visibility = if (showHeader) View.VISIBLE else View.INVISIBLE
            holder.header.visibility = visibility
        }
    }

    /**
     * Sets the onContactSelectedListener by a given one
     *
     * @param onContactSelectedListener
     */
    fun setOnContactSelectedListener(onContactSelectedListener: OnContactSelectedListener?) {
        mOnContactSelectedListener = onContactSelectedListener
    }

    /**
     * The interface for the onContactSelectedListener
     */
    interface OnContactSelectedListener {
        fun onContactSelected(normPhoneNumber: String?)
    }
}