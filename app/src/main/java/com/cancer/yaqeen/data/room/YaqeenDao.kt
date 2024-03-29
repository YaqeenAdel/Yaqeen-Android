package com.cancer.yaqeen.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cancer.yaqeen.data.features.home.articles.room.Article

@Dao
interface YaqeenDao {

    @Query("SELECT * FROM Article")
    suspend fun getArticles(): List<Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>): List<Long>

    @Query("DELETE FROM Article WHERE articleID = :articleID")
    suspend fun removeArticle(articleID: Int): Int

    @Query("DELETE FROM Article")
    suspend fun removeArticles(): Int

}