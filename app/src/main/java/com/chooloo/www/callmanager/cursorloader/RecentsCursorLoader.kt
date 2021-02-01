/*
 * Copyright (C) 2017 The Android Open Source Project
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
 */
package com.chooloo.www.callmanager.cursorloader

import android.content.Context
import android.provider.CallLog
import androidx.loader.content.CursorLoader
import java.util.*
import java.util.stream.Collectors

class RecentsCursorLoader
/**
 * Constructor
 *
 * @param context     calling context
 * @param phoneNumber String
 * @param contactName String
 */
(context: Context?, phoneNumber: String?, contactName: String?) : CursorLoader(
        context!!,
        CallLog.Calls.CONTENT_URI.buildUpon().build(),
        RECENTS_PROJECTION,
        getSelection(contactName, phoneNumber),
        null,
        RECENTS_ORDER) {
    companion object {
        // Columns
        @JvmField
        var COLUMN_ID = CallLog.Calls._ID
        @JvmField
        var COLUMN_NUMBER = CallLog.Calls.NUMBER
        @JvmField
        var COLUMN_DATE = CallLog.Calls.DATE
        @JvmField
        var COLUMN_DURATION = CallLog.Calls.DURATION
        @JvmField
        var COLUMN_TYPE = CallLog.Calls.TYPE
        var COLUMN_CACHED_NAME = CallLog.Calls.CACHED_NAME
        private val RECENTS_ORDER = COLUMN_DATE + " DESC"

        /**
         * Cursor selection string
         * Column to load
         */
        private val RECENTS_PROJECTION = arrayOf(
                COLUMN_ID,
                COLUMN_NUMBER,
                COLUMN_DATE,
                COLUMN_DURATION,
                COLUMN_TYPE,
                COLUMN_CACHED_NAME
        )

        /**
         * Return a selection query for the cursor
         * According to given name and phone number
         * By default they're set to "", if they're not null, than they'le be set to their value
         *
         * @param contactName name of the contact duh
         * @param phoneNumber duhh
         * @return String sql style string
         */
        private fun getSelection(contactName: String?, phoneNumber: String?): String {
            val conditions: MutableList<String> = ArrayList()
            if (contactName != null) conditions.add(COLUMN_CACHED_NAME + " LIKE '%" + contactName + "%'")
            if (phoneNumber != null) conditions.add(COLUMN_NUMBER + " LIKE '%" + phoneNumber + "%'")
            return if (conditions.size == 0) "" else conditions.stream().collect(Collectors.joining(" AND "))
        }
    }
}