package com.chooloo.www.callmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.chooloo.www.callmanager.database.entity.Contact

@Dao
interface ContactDao {
    @Insert
    fun insert(vararg contacts: Contact?)

    @Insert
    fun insert(contacts: List<Contact?>?)

    @Update
    fun update(vararg contacts: Contact?)

    @Query("DELETE FROM contact_table")
    fun deleteAll()

    @Query("DELETE FROM contact_table WHERE phone_numbers LIKE '%' || :phoneNumber || '%'")
    fun deleteByPhoneNumber(phoneNumber: String?): Int

    @Query("DELETE FROM contact_table WHERE contact_id = :id")
    fun deleteById(id: Long): Int

    @Query("SELECT * from contact_table WHERE phone_numbers LIKE '%' || :phoneNumber || '%'")
    fun getContactsByPhoneNumber(phoneNumber: String?): LiveData<List<Contact?>?>?

    @get:Query("SELECT * from contact_table ORDER BY contact_id ASC")
    val allContacts: LiveData<List<Contact?>?>?

    @Query("SELECT * from contact_table WHERE list_id LIKE :listId")
    fun getContactsInList(listId: Long): LiveData<List<Contact?>?>?
}