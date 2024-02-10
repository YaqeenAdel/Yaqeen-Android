package com.cancer.yaqeen.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cancer.yaqeen.data.features.home.articles.room.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
abstract class YaqeenDatabase: RoomDatabase() {

    abstract fun yaqeenDao(): YaqeenDao

}