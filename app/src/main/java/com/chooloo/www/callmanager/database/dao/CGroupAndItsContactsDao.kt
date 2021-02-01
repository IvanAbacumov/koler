package com.chooloo.www.callmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.chooloo.www.callmanager.database.entity.CGroupAndItsContacts

@Dao
interface CGroupAndItsContactsDao {
    @get:Transaction
    @get:Query("SELECT * from cgroup_table")
    val allCGroupsAndTheirContacts: LiveData<List<CGroupAndItsContacts?>?>?
}