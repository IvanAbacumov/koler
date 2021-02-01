package com.chooloo.www.callmanager.cursorloader

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import android.telephony.PhoneNumberUtils
import androidx.loader.content.CursorLoader
import com.chooloo.www.callmanager.database.entity.Contact

class ContactLookupCursorLoader(context: Context, phoneNumber: String) : CursorLoader(
        context,
        buildUri(context, phoneNumber), arrayOf(
        COLUMN_ID,
        COLUMN_NAME,
        COLUMN_NUMBER),
        ContactsCursorLoader.where,
        null,
        PhoneLookup.DISPLAY_NAME_PRIMARY + " ASC"
) {
    fun loadContact(): Contact? {
        val cursor = super.loadInBackground()
        if (cursor == null || cursor.count == 0) {
            return null
        }
        cursor.moveToFirst()
        val contact = Contact(
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER))
        )
        cursor.close()
        return contact
    }

    companion object {
        private const val COLUMN_ID = PhoneLookup.CONTACT_ID
        private const val COLUMN_NAME = PhoneLookup.DISPLAY_NAME_PRIMARY
        private const val COLUMN_NUMBER = PhoneLookup.NUMBER
        private fun buildUri(context: Context, phoneNumber: String): Uri {
            val builder = PhoneLookup.CONTENT_FILTER_URI.buildUpon()
            builder.appendPath(PhoneNumberUtils.normalizeNumber(phoneNumber))
            builder.appendQueryParameter(ContactsContract.STREQUENT_PHONE_ONLY, "true")
            builder.appendQueryParameter(ContactsContract.REMOVE_DUPLICATE_ENTRIES, "true")
            builder.appendQueryParameter(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX, "true")
            return builder.build()
        }
    }
}