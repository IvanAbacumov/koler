package com.chooloo.www.callmanager.cursorloader

import android.Manifest.permission
import android.content.Context
import android.database.Cursor
import android.database.MergeCursor
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import com.chooloo.www.callmanager.util.PermissionUtils
import timber.log.Timber
import java.util.*

/**
 * Extends the basic ContactsCursorLoader but also adds the favourite contacts to it
 */
class FavoritesAndContactsLoader(context: Context?, phoneNumber: String, contactName: String, withFavs: Boolean) : ContactsCursorLoader(context, phoneNumber, contactName) {
    private var mWithFavs = true
    override fun loadInBackground(): Cursor? {
        // get only contacts
        val contactsCursor = loadContacts()
        if (!mWithFavs) return contactsCursor

        // Handle favourites too
        val cursors: MutableList<Cursor?> = ArrayList()
        val favoritesCursor = loadFavorites()
        val favoritesCount = favoritesCursor?.count ?: 0

        // add the cursors
        cursors.add(favoritesCursor)
        cursors.add(contactsCursor)

        // merge cursors
        return object : MergeCursor(cursors.toTypedArray()) {
            override fun getExtras(): Bundle {
                // Need to get the extras from the contacts cursor.
                val extras = if (contactsCursor == null) Bundle() else contactsCursor.extras
                extras.putInt(FAVORITES_COUNT, favoritesCount)
                return extras
            }
        }
    }

    /**
     * Try to load the contacts, handle the exceptions
     *
     * @return The contacts cursor
     */
    private fun loadContacts(): Cursor? {
        // ContactsCursor.loadInBackground() can return null; MergeCursor
        // correctly handles null cursors.
        return try {
            super.loadInBackground()
        } catch (e: NullPointerException) {
            // Ignore NPEs, SQLiteExceptions and SecurityExceptions thrown by providers
            null
        } catch (e: SQLiteException) {
            null
        } catch (e: SecurityException) {
            null
        }
    }

    /**
     * Load the favorite contacts
     *
     * @return The cursor containing the favorites
     */
    private fun loadFavorites(): Cursor? {
        PermissionUtils.checkPermissionsGranted(context, arrayOf(permission.READ_CONTACTS), true)
        val selection = COLUMN_STARRED + " = 1"
        return context.contentResolver.query(
                buildFavoritesUri(),
                projection,
                selection,
                null,
                sortOrder)
    }

    companion object {
        const val FAVORITES_COUNT = "favorites_count"

        /**
         * Builds contact uri by given name and phone number
         *
         * @return Builder.build()
         */
        private fun buildFavoritesUri(): Uri {
            val builder = Phone.CONTENT_URI.buildUpon()
            builder.appendQueryParameter(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX, "true")
            builder.appendQueryParameter(ContactsContract.REMOVE_DUPLICATE_ENTRIES, "true")
            return builder.build()
        }
    }

    /**
     * Constructor
     *
     * @param context     calling context
     * @param phoneNumber String
     * @param contactName String
     */
    init {
        Timber.i("Creating contacts loader with $phoneNumber : $contactName")
        mWithFavs = withFavs
    }
}