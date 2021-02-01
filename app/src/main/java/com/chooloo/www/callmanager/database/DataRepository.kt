package com.chooloo.www.callmanager.database

import android.os.Process
import androidx.lifecycle.LiveData
import com.chooloo.www.callmanager.database.entity.CGroup
import com.chooloo.www.callmanager.database.entity.CGroupAndItsContacts
import com.chooloo.www.callmanager.database.entity.Contact
import com.chooloo.www.callmanager.util.Utilities

class DataRepository private constructor(private val mDatabase: AppDatabase) {
    // - Insert - //
    fun insertCGroups(vararg cGroup: CGroup?): LongArray? {
        return if (Utilities.isInUIThread()) {
            //TODO start in thread
            null
        } else {
            mDatabase.cGroupDao!!.insert(*cGroup)
        }
    }

    fun insertContacts(contacts: List<Contact?>?) {
        if (Utilities.isInUIThread()) {
            //TODO start in thread
        } else {
            mDatabase.contactDao!!.insert(contacts)
        }
    }

    // - Update - //
    fun update(vararg contacts: Contact?) {
        val thread = Thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            mDatabase.contactDao!!.update(*contacts)
        }
        thread.start()
    }

    // - Delete - //
    fun deleteContact(contactId: Long) {
        val thread = Thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            mDatabase.contactDao!!.deleteById(contactId)
        }
        thread.start()
    }

    fun deleteCGroup(listId: Long) {
        val thread = Thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            mDatabase.cGroupDao!!.deleteById(listId)
        }
        thread.start()
    }

    // - Query - //
    val allContacts: LiveData<List<Contact?>?>?
        get() = mDatabase.contactDao!!.allContacts

    fun getContactsInList(list: CGroup): LiveData<List<Contact?>?>? {
        return mDatabase.contactDao!!.getContactsInList(list.listId)
    }

    fun getContactsInList(listId: Long): LiveData<List<Contact?>?>? {
        return mDatabase.contactDao!!.getContactsInList(listId)
    }

    val allCGroups: LiveData<List<CGroup?>?>?
        get() = mDatabase.cGroupDao!!.allCGroups

    fun getCGroup(listId: Long): LiveData<List<CGroup?>?>? {
        return mDatabase.cGroupDao!!.getCGroupById(listId)
    }

    val allCGroupsAndTheirContacts: LiveData<List<CGroupAndItsContacts?>?>?
        get() = mDatabase.cGroupAndItsContactsDao!!.allCGroupsAndTheirContacts

    companion object {
        private var sInstance: DataRepository? = null
        @JvmStatic
        fun getInstance(database: AppDatabase): DataRepository? {
            if (sInstance == null) {
                synchronized(DataRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = DataRepository(database)
                    }
                }
            }
            return sInstance
        }
    }
}