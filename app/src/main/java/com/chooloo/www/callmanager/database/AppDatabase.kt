package com.chooloo.www.callmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chooloo.www.callmanager.database.dao.CGroupAndItsContactsDao
import com.chooloo.www.callmanager.database.dao.CGroupDao
import com.chooloo.www.callmanager.database.dao.ContactDao
import com.chooloo.www.callmanager.database.entity.CGroup
import com.chooloo.www.callmanager.database.entity.Contact

@Database(entities = [CGroup::class, Contact::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val cGroupDao: CGroupDao?
    abstract val contactDao: ContactDao?
    abstract val cGroupAndItsContactsDao: CGroupAndItsContactsDao?

    companion object {
        private var sInstance: AppDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): AppDatabase? {
            if (sInstance == null || !sInstance!!.isOpen) {
                synchronized(AppDatabase::class.java) {
                    if (sInstance == null || !sInstance!!.isOpen) {
                        sInstance = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java, "app_database")
                                .fallbackToDestructiveMigration()
                                .build()
                    }
                }
            }
            return sInstance
        }
    }
}