package com.cancer.yaqeen.data.features.home

import android.util.Log
import com.bumptech.glide.load.engine.Resource
import com.cancer.yaqeen.data.features.home.IHomeRepository
import com.cancer.yaqeen.data.features.home.mappers.MappingAddArticleToFavouriteRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.mappers.MappingArticlesRemoteAsModel
import com.cancer.yaqeen.data.features.home.models.Article
import com.cancer.yaqeen.data.features.home.requests.AddArticleToFavouriteRequest
import com.cancer.yaqeen.data.features.home.requests.RemoveArticleFromFavouriteRequest
import com.cancer.yaqeen.data.features.home.responses.HomeArticlesResponse
import com.cancer.yaqeen.data.features.onboarding.mappers.MappingResourcesRemoteAsModel
import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.base.flowStatus
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: YaqeenAPI,
    errorHandler: ErrorHandlerImpl,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IHomeRepository {


    override suspend fun getHomeArticles(searchQuery: String): Flow<DataState<List<Article>>> =
        flowStatus {
            getResultRestAPI(MappingArticlesRemoteAsModel()){
                apiService.getHomeArticles(prefEncryptionUtil.selectedLanguage, "%$searchQuery%")
            }
        }

    override suspend fun addArticleToFavourite(request: AddArticleToFavouriteRequest): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingAddArticleToFavouriteRemoteAsUIModel()){
                apiService.addArticleToFavourite(request)
            }
        }


}