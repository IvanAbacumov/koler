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
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.telephony.PhoneNumberUtils
import androidx.loader.content.CursorLoader

class NewContactsCursorLoader
/**
 * Constructor
 *
 * @param context     calling context
 * @param phoneNumber String
 * @param contactName String
 */
(context: Context?, phoneNumber: String, contactName: String) : CursorLoader(
        context!!,
        buildUri(),
        CONTACTS_PROJECTION,
        getSelection(phoneNumber, contactName),
        null,
        CONTACTS_ORDER) {
    companion object {
        // Columns
        var COLUMN_ID = ContactsContract.Contacts._ID
        var COLUMN_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        var COLUMN_THUMBNAIL = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        var COLUMN_NUMBER = Phone.NORMALIZED_NUMBER
        var COLUMN_STARRED = Phone.STARRED
        private const val CONTACTS_ORDER = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC"

        /**
         * Cursor selection string
         * Columns to load
         */
        private val CONTACTS_PROJECTION = arrayOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_THUMBNAIL,
                COLUMN_NUMBER,
                COLUMN_STARRED
        )

        /**
         * Return a selection query for the cursor
         * According to given name and phone number
         * By default they're set to "", if they're not null, than they'le be set to their value
         *
         * @return String sql style
         */
        private fun getSelection(phoneNumber: String, contactName: String): String {
            var phoneNumber: String? = phoneNumber
            var contactName: String? = contactName
            phoneNumber = PhoneNumberUtils.normalizeNumber(phoneNumber)
            if (phoneNumber == null) phoneNumber = ""
            if (contactName == null) contactName = ""
            return "((" + COLUMN_NAME + " IS NOT NULL" +
                    " AND " + COLUMN_NAME + " LIKE '%" + contactName + "%')" +
                    " OR (" + Phone.DISPLAY_NAME_ALTERNATIVE + " IS NOT NULL" +
                    " AND " + Phone.DISPLAY_NAME_ALTERNATIVE + " LIKE '%" + contactName + "%'))" +
                    " AND " + COLUMN_NUMBER + " LIKE '%" + phoneNumber + "%'" +
                    " AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1" +
                    " AND (" + ContactsContract.RawContacts.ACCOUNT_NAME + " IS NULL" +
                    " OR ( " + ContactsContract.RawContacts.ACCOUNT_TYPE + " NOT LIKE '%whatsapp%'" +
                    " AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " NOT LIKE '%tachyon%'" + "))"
        }

        /**
         * Builds contact uri with appropriate queries parameters
         *
         * @return Builder.build()
         */
        private fun buildUri(): Uri {
            val builder = Phone.CONTENT_URI.buildUpon()
            builder.appendQueryParameter(ContactsContract.STREQUENT_PHONE_ONLY, "true")
            builder.appendQueryParameter(ContactsContract.PRIMARY_ACCOUNT_NAME, "true")
            builder.appendQueryParameter(ContactsContract.REMOVE_DUPLICATE_ENTRIES, "true")
            builder.appendQueryParameter(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX, "true")
            return builder.build()
        }
    }
}