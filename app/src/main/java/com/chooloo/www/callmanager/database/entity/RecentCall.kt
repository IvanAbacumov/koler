package com.chooloo.www.callmanager.database.entity

import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import android.text.format.DateFormat
import com.chooloo.www.callmanager.cursorloader.RecentsCursorLoader
import com.chooloo.www.callmanager.util.ContactUtils
import timber.log.Timber
import java.text.DateFormatSymbols
import java.util.*

class RecentCall {
    // Attributes
    private var mContext: Context
    var callId: Long = 0
        private set
    var callerName: String
        private set
    var callerNumber: String
        private set
    var callType: Int
        private set
    var callDuration: String
        private set
    var callDate: Date
        private set
    var count = 0
        private set

    /**
     * Constructor
     *
     * @param number   caller's number
     * @param type     call's type (out/in/missed)
     * @param duration call's duration
     * @param date     call's date
     */
    constructor(context: Context, number: String, type: Int, duration: String, date: Date) {
        mContext = context
        callerNumber = number
        callerName = ContactUtils.lookupContact(context, number).name
        callType = type
        callDuration = duration
        callDate = date
    }

    constructor(context: Context, cursor: Cursor) {
        mContext = context
        callId = cursor.getLong(cursor.getColumnIndex(RecentsCursorLoader.COLUMN_ID))
        callerNumber = cursor.getString(cursor.getColumnIndex(RecentsCursorLoader.COLUMN_NUMBER))
        Timber.i("Recent Call Number: " + callerNumber)
        callerName = ContactUtils.lookupContact(context, callerNumber).name
        Timber.i("Recent Call Name: " + callerName)
        callDuration = cursor.getString(cursor.getColumnIndex(RecentsCursorLoader.COLUMN_DURATION))
        callDate = Date(cursor.getLong(cursor.getColumnIndex(RecentsCursorLoader.COLUMN_DATE)))
        callType = cursor.getInt(cursor.getColumnIndex(RecentsCursorLoader.COLUMN_TYPE))
        count = checkNextMutliple(cursor)
        cursor.moveToPosition(cursor.position)
    }

    /**
     * Return a string representing the date of the call relatively to the current time
     *
     * @return String
     */
    val callDateString: String
        get() {
            val dateFormat = DateFormat()
            return DateFormat.format("yy ", callDate).toString() +
                    DateFormatSymbols().shortMonths[DateFormat.format("MM", callDate).toString().toInt() - 1] +
                    DateFormat.format(" dd, hh:mm", callDate).toString()
        }

    /**
     * Check how many calls from the same contact are there from the current entry
     *
     * @param cursor
     * @return Amount of the calls from the same contact in a row
     */
    fun checkNextMutliple(cursor: Cursor): Int {
        var count = 1
        while (true) {
            try {
                cursor.moveToNext()
                if (cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)) == callerNumber) count++ else return count
            } catch (e: Exception) { // probably index out of bounds exception
                return count
            }
        }
    }

    companion object {
        // Call Types
        const val TYPE_OUTGOING = CallLog.Calls.OUTGOING_TYPE
        const val TYPE_INCOMING = CallLog.Calls.INCOMING_TYPE
        const val TYPE_MISSED = CallLog.Calls.MISSED_TYPE
        const val TYPE_VOICEMAIL = CallLog.Calls.VOICEMAIL_TYPE
        const val TYPE_REJECTED = CallLog.Calls.REJECTED_TYPE
    }
}