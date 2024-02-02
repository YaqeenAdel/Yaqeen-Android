package com.cancer.yaqeen.data.features.home.articles

import com.cancer.yaqeen.data.features.home.articles.models.Article
import com.cancer.yaqeen.data.features.home.articles.requests.BookmarkArticleRequest
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
     suspend fun getHomeArticles(searchQuery: String): Flow<DataState<List<Article>>>
     suspend fun getBookmarkedArticles(): Flow<DataState<List<Article>>>
     suspend fun bookmarkArticle(request: BookmarkArticleRequest): Flow<DataState<Boolean>>
     suspend fun unBookmarkArticle(bookmarkId: Int): Flow<DataState<Boolean>>

}