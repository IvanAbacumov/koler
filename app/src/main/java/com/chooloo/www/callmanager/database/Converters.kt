package com.chooloo.www.callmanager.database

import androidx.room.TypeConverter
import com.chooloo.www.callmanager.util.Utilities
import java.util.*

object Converters {
    @TypeConverter
    fun listToString(list: List<String?>?): String {
        return Utilities.joinStringsWithSeparator(list!!, ";")
    }

    @TypeConverter
    fun stringToList(str: String): List<String> {
        val arr = str.split(";").toTypedArray()
        return Arrays.asList(*arr)
    }
}