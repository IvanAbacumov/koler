package com.chooloo.www.callmanager.database.entity

import androidx.room.Embedded
import androidx.room.Relation

class CGroupAndItsContacts(@field:Embedded var cgroup: CGroup) {

    @Relation(parentColumn = "list_id", entityColumn = "list_id")
    var contacts: List<Contact>? = null

}