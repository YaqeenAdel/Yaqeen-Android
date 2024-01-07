package com.cancer.yaqeen.data.features.home

import com.cancer.yaqeen.data.features.home.responses.HomeArticlesResponse
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
     suspend fun getHomeArticles(): Flow<DataState<HomeArticlesResponse>>

}