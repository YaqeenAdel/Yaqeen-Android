package com.cancer.yaqeen.data.room.converters

import androidx.room.TypeConverter

class UUIDConverters {
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        return if (data.isEmpty())
            listOf()
        else
            data.split(",").map { it }
    }
}