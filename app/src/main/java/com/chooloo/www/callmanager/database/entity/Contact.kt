package com.chooloo.www.callmanager.database.entity

import android.database.Cursor
import androidx.room.*
import com.chooloo.www.callmanager.cursorloader.ContactsCursorLoader
import com.chooloo.www.callmanager.util.Utilities
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*

@Entity(tableName = "contact_table", indices = [Index("list_id")], foreignKeys = [ForeignKey(entity = CGroup::class, parentColumns = ["list_id"], childColumns = ["list_id"], onDelete = ForeignKey.CASCADE)])
class Contact : Serializable {
    /**
     * Returns the contact's id
     *
     * @return the contact's id
     */
    /**
     * Sets the contact's id by a given id
     *
     * @param contactId
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "contact_id")
    var contactId: Long = 0
    /**
     * Returns the contact's list id
     *
     * @return long
     */
    /**
     * Sets the contact's list id by a given number
     *
     * @param listId
     */
    @ColumnInfo(name = "list_id")
    var listId: Long = 0
    /**
     * Returns the contact's name
     *
     * @return String of the name
     */
    /**
     * Sets the contact's name by a given String
     *
     * @param name
     */
    @ColumnInfo(name = "full_name")
    var name: String

    @ColumnInfo(name = "phone_numbers")
    private var phoneNumbers: MutableList<String?>
    /**
     * Returns the contact's image (Uri)
     *
     * @return String
     */
    /**
     * Sets the contact's image by a given image (String)
     *
     * @param photoUri
     */
    @Ignore
    var photoUri // No need to save this to the database
            : String? = null
    /**
     * Returns wither the contact is a favorite contact
     *
     * @return
     */
    /**
     * Makes the contact favorite/not favorite
     *
     * @param isFavorite
     * @return
     */
    @ColumnInfo(name = "is_favorite")
    var isFavorite = false

    /**
     * Contact constructor
     * Accepts a name and a list of numbers (without an image)
     *
     * @param name
     * @param phoneNumbers
     */
    constructor(name: String, phoneNumbers: MutableList<String?>) {
        this.name = name
        this.phoneNumbers = phoneNumbers
    }

    /**
     * Get only name and phone number
     * Add the phoneNumber for a list of phone numbers for the sake of consistancy
     *
     * @param name
     * @param phoneNumber
     */
    constructor(name: String, phoneNumber: String?) {
        this.name = name
        phoneNumbers = ArrayList()
        phoneNumbers.add(phoneNumber)
    }

    /**
     * Contact constructor
     * Accepts a name, a list of numbers and an image
     *
     * @param name
     * @param phoneNumbers
     * @param photoUri
     */
    @Ignore
    constructor(name: String, phoneNumbers: MutableList<String?>, photoUri: String?) {
        this.name = name
        this.phoneNumbers = phoneNumbers
        this.photoUri = photoUri
    }

    /**
     * Contact constructor
     * Accepts a name, one phone number and an image
     *
     * @param name        the contact's name
     * @param phoneNumber the contact's phone number
     * @param photoUri    the contact's image
     */
    @Ignore
    constructor(name: String, phoneNumber: String, photoUri: String?) {
        this.name = name
        this.photoUri = photoUri
        phoneNumbers = ArrayList()
        phoneNumbers.add(phoneNumber)
    }

    /**
     * Contact constructor
     * Accepts a name, one phone number and an image
     *
     * @param id          the contact's id
     * @param name        the contact's name
     * @param phoneNumber the contact's phone number
     * @param photoUri    the contact's image
     */
    @Ignore
    constructor(id: Long, name: String, phoneNumber: String, photoUri: String?) {
        contactId = id
        this.name = name
        this.photoUri = photoUri
        phoneNumbers = ArrayList()
        phoneNumbers.add(phoneNumber)
    }

    /**
     * Contact constructor
     * Accepts a cursor from with the function loads the content by itself
     *
     * @param cursor
     */
    @Ignore
    constructor(cursor: Cursor) {
        contactId = cursor.getLong(cursor.getColumnIndex(ContactsCursorLoader.COLUMN_ID))
        name = cursor.getString(cursor.getColumnIndex(ContactsCursorLoader.COLUMN_NAME))
        photoUri = cursor.getString(cursor.getColumnIndex(ContactsCursorLoader.COLUMN_THUMBNAIL))
        phoneNumbers = ArrayList()
        phoneNumbers.add(cursor.getString(cursor.getColumnIndex(ContactsCursorLoader.COLUMN_NUMBER)))
        isFavorite = "1" == cursor.getString(cursor.getColumnIndex(ContactsCursorLoader.COLUMN_STARRED))
    }

    /**
     * Returns all the phone numbers of a contact
     *
     * @return List<String>
    </String> */
    fun getPhoneNumbers(): List<String?> {
        return phoneNumbers
    }// The number cant be decoded so its probably not needed anyway// Try decoding it just in case

    /**
     * Returns the contact's main phone number
     *
     * @return String
     */
    val mainPhoneNumber: String?
        get() {
            if (phoneNumbers.isEmpty()) return null
            var phoneNumber = phoneNumbers[0]

            // Try decoding it just in case
            try {
                phoneNumber = URLDecoder.decode(phoneNumber, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                // The number cant be decoded so its probably not needed anyway
            }
            return phoneNumber
        }

    /**
     * Sets the contact's phone numbers by a given list of strings
     *
     * @param phoneNumbers
     */
    fun setPhoneNumbers(phoneNumbers: MutableList<String?>) {
        this.phoneNumbers = phoneNumbers
    }

    /**
     * Returns the contact's details in a string
     *
     * @return a string representing the contact
     */
    override fun toString(): String {
        return String.format(Utilities.sLocale, "id: %d, list_id: %d, name: %s, numbers: %s", contactId, listId, name, phoneNumbers.toString())
    }

    /**
     * Check if self equals a given Contact object
     *
     * @param obj
     * @return boolean is equals / not
     */
    override fun equals(obj: Any?): Boolean {
        if (super.equals(obj)) return true
        if (obj !is Contact) return false
        val c = obj
        return name == c.name && phoneNumbers == c.getPhoneNumbers()
    }
}