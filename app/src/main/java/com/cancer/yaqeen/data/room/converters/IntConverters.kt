package com.cancer.yaqeen.data.room.converters

import androidx.room.TypeConverter

class IntConverters {
    @TypeConverter
    fun fromList(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): List<Int> {
        return if (data.isEmpty())
            listOf()
        else
            data.split(",").map { it.toInt() }
    }
}