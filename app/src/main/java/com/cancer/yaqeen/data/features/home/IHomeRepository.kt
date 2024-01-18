package com.cancer.yaqeen.data.features.home

import com.cancer.yaqeen.data.features.home.models.Article
import com.cancer.yaqeen.data.features.home.models.Bookmark
import com.cancer.yaqeen.data.features.home.requests.AddArticleToFavouriteRequest
import com.cancer.yaqeen.data.features.home.requests.RemoveArticleFromFavouriteRequest
import com.cancer.yaqeen.data.features.home.responses.HomeArticlesResponse
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
     suspend fun getHomeArticles(searchQuery: String): Flow<DataState<List<Article>>>
     suspend fun getBookmarkedArticles(): Flow<DataState<List<Bookmark>>>
     suspend fun addArticleToFavourite(request: AddArticleToFavouriteRequest): Flow<DataState<Boolean>>

}