package com.chooloo.www.callmanager.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cgroup_table")
class CGroup(@field:ColumnInfo(name = "name") var name: String) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_id")
    var listId: Long = 0

    override fun equals(obj: Any?): Boolean {
        if (super.equals(obj)) return true
        if (obj is CGroup) {
            return name == obj.name
        }
        return false
    }
}