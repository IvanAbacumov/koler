/*
 * Copyright (C) 2014 skyfish.jy@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.chooloo.www.callmanager.adapter

import android.content.Context
import android.database.Cursor
import android.database.DataSetObserver
import android.provider.ContactsContract
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by skyfishjy on 10/31/14.
 */
abstract class AbsCursorRecyclerViewAdapter<VH : RecyclerView.ViewHolder?>(protected var mContext: Context,
                                                                           /**
                                                                            * Returns the cursor
                                                                            *
                                                                            * @return
                                                                            */
                                                                           var cursor: Cursor?
) : RecyclerView.Adapter<VH>() {
    private var mDataValid: Boolean
    private var mRowIdColumn = 0
    private val mDataSetObserver: DataSetObserver?
    abstract fun onBindViewHolder(viewHolder: VH, cursor: Cursor?)

    /**
     * Returns the cursors counts
     *
     * @return
     */
    override fun getItemCount(): Int {
        return if (mDataValid && cursor != null) cursor!!.count else 0
    }

    /**
     * Returns an item id by position
     *
     * @param position
     * @return
     */
    override fun getItemId(position: Int): Long {
        return if (mDataValid && cursor != null && cursor!!.moveToPosition(position)) cursor!!.getLong(mRowIdColumn) else 0
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        check(mDataValid) { "this should only be called when the cursor is valid" }
        check(cursor!!.moveToPosition(position)) { "couldn't move cursor to position $position" }
        onBindViewHolder(viewHolder, cursor)
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    open fun changeCursor(cursor: Cursor) {
        val old = swapCursor(cursor)
        old?.close()
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * [.changeCursor], the returned old Cursor is *not*
     * closed.
     */
    private fun swapCursor(newCursor: Cursor): Cursor? {
        if (newCursor === cursor) return null // cursor hasn't changed
        val oldCursor = cursor
        if (oldCursor != null && mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver)
        cursor = newCursor
        if (cursor != null) {
            if (mDataSetObserver != null) cursor!!.registerDataSetObserver(mDataSetObserver)
            mRowIdColumn = try {
                newCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            } catch (e: IllegalArgumentException) {
                newCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            }
            mDataValid = true
            notifyDataSetChanged()
        } else {
            mRowIdColumn = -1
            mDataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor
    }

    private inner class NotifyingDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            mDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            mDataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }

    /**
     * Constructor
     *
     * @param context
     * @param cursor
     */
    init {
        mDataValid = cursor != null
        mRowIdColumn = try {
            if (mDataValid) cursor!!.getColumnIndex(ContactsContract.Contacts._ID) else -1
        } catch (e: IllegalArgumentException) {
            if (mDataValid) cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID) else -1
        }
        mDataSetObserver = NotifyingDataSetObserver()
        if (cursor != null) cursor!!.registerDataSetObserver(mDataSetObserver)
    }
}