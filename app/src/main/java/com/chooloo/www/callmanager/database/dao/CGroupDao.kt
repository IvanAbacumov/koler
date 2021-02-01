package com.chooloo.www.callmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chooloo.www.callmanager.database.entity.CGroup

@Dao
interface CGroupDao {
    @Insert
    fun insert(vararg lists: CGroup?): LongArray?

    @Query("DELETE FROM cgroup_table")
    fun deleteAll(): Int

    @Query("DELETE FROM cgroup_table WHERE name LIKE :name")
    fun deleteByName(name: String?): Int

    @Query("DELETE FROM cgroup_table WHERE list_id LIKE :listId")
    fun deleteById(listId: Long): Int

    @Query("SELECT * from cgroup_table WHERE list_id LIKE :listId")
    fun getCGroupById(listId: Long): LiveData<List<CGroup?>?>?

    @Query("SELECT * from cgroup_table WHERE name LIKE :name")
    fun getCGroupByName(name: String?): LiveData<List<CGroup?>?>?

    @get:Query("SELECT * from cgroup_table ORDER BY list_id ASC")
    val allCGroups: LiveData<List<CGroup?>?>?
}